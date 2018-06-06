/*
 * *************************************************************************************************
 *                                 Copyright 2017 Universum Studios
 * *************************************************************************************************
 *                  Licensed under the Apache License, Version 2.0 (the "License")
 * -------------------------------------------------------------------------------------------------
 * You may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 * *************************************************************************************************
 */
package universum.studios.websocket.adapter;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;

/**
 * An {@link InputStream} implementation used by {@link WebSocketAdapter} as its input stream.
 *
 * @author Martin Albedinsky
 * @since 1.0
 *
 * @see WebSocketOutputStream
 */
final class WebSocketInputStream extends InputStream implements WebSocketDelegate.OnIncomingFrameListener {

	/*
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	@SuppressWarnings("unused")
	private static final String TAG = "WebSocketInputStream";

	/**
	 * Initial size for queue storing received data in buckets, that is complete payload per frame.
	 */
	private static final int DATA_QUEUE_INITIAL_SIZE = 5;

	/*
	 * Interface ===================================================================================
	 */

	/*
	 * Static members ==============================================================================
	 */

	/*
	 * Members =====================================================================================
	 */

	/**
	 * Boolean flag indicating whether this stream is closed or not.
	 */
	private final AtomicBoolean closed = new AtomicBoolean(false);

	/**
	 * Parent socket using this stream as its input.
	 */
	private final Closeable socket;

	/**
	 * Delegate from which is this stream receiving all frame's payload data and making them available
	 * via its read methods.
	 */
	private final WebSocketDelegate delegate;

	/**
	 * Buffer storing all data from the received frames via {@link #onFrameReceived(WebSocketDelegate.Frame)}
	 * which were not marked as final frames.
	 *
	 * @see WebSocketDelegate.Frame#isFinal()
	 */
	private byte[] dataBuffer = new byte[0];

	/**
	 * Queue used to store payload data of the received frames which are processed/read one at a time.
	 *
	 * @see WebSocketDelegate.Frame#getPayload()
	 */
	private final Queue<byte[]> dataQueue;

	/**
	 * Lock used for synchronized operations upon {@link #dataQueue}.
	 */
	private final Object dataLock = new Object();

	/**
	 * Internal stream used for reading of received data.
	 */
	private ByteArrayInputStream stream;

	/**
	 * Count down latch that is used to lock reading of this stream's data until there are some
	 * new data to be read.
	 */
	private CountDownLatch countDownLatch;

	/*
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of WebSocketInputStream for the given <var>socket</var> and <var>delegate</var>.
	 *
	 * @param socket   The parent socket that will use this stream as its input.
	 * @param delegate The delegate to which should this stream register as incoming frame listener
	 *                 and make all received payload data available via its read methods.
	 */
	WebSocketInputStream(final Closeable socket, final WebSocketDelegate delegate) {
		super();
		this.socket = socket;
		this.delegate = delegate;
		this.delegate.registerOnIncomingFrameListener(this);
		this.dataQueue = new LinkedBlockingQueue<>(DATA_QUEUE_INITIAL_SIZE);
	}

	/*
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override public void onFrameReceived(@Nonnull final WebSocketDelegate.Frame frame) {
		if (closed.get()) {
			return;
		}
		synchronized (dataLock) {
			final byte[] payload = frame.getPayload();
			// Expand the existing buffer and write the payload data into it.
			final byte[] newBuffer = new byte[dataBuffer.length + payload.length];
			System.arraycopy(dataBuffer, 0, newBuffer, 0, dataBuffer.length);
			System.arraycopy(payload, 0, newBuffer, dataBuffer.length, payload.length);
			this.dataBuffer = newBuffer;
			if (frame.isFinal() && dataBuffer.length > 0) {
				addData(dataBuffer);
				this.dataBuffer = new byte[0];
			}
		}
	}

	/**
	 * Receives the specified <var>bytes</var> to be made available for reading. If there are already
	 * some data read at this time, the specified data will be queued and made available for reading
	 * later. If there are no data read at this time the specified data will be made available for
	 * reading instantly.
	 *
	 * @param bytes The bytes to be received and made available for reading via this stream.
	 */
	private void addData(byte[] bytes) {
		if (stream == null) {
			this.stream = new ByteArrayInputStream(bytes);
			if (countDownLatch != null && countDownLatch.getCount() > 0) {
				this.countDownLatch.countDown();
			}
			this.countDownLatch = null;
		} else {
			dataQueue.add(bytes);
		}
	}

	/**
	 */
	@Override public synchronized int available() throws IOException {
		assertOpenedOrThrowException();
		return stream == null ? 0 : stream.available();
	}


	/**
	 */
	@Override public synchronized long skip(final long n) throws IOException {
		assertOpenedOrThrowException();
		return stream == null ? 0 : stream.skip(n);
	}

	/**
	 */
	@Override public boolean markSupported() {
		return true;
	}

	/**
	 */
	@Override public synchronized void mark(final int i) {
		if (stream != null) stream.mark(i);
	}

	/**
	 */
	@Override public synchronized void reset() throws IOException {
		if (stream != null) stream.reset();
	}

	/**
	 */
	@Override public synchronized int read() throws IOException {
		assertOpenedOrThrowException();
		if (stream != null) {
			final int b = stream.read();
			if (b == -1) {
				this.stream = null;
			}
			return b;
		}
		// No more data in the stream, check if we have some queued data.
		synchronized (dataLock) {
			if (dataQueue.size() > 0) {
				this.stream = new ByteArrayInputStream(dataQueue.poll());
			} else {
				this.stream = null;
				this.countDownLatch = new CountDownLatch(1);
			}
		}
		if (stream == null) {
			// We do not have any data to read from, wait for the new one.
			try {
				this.countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		final int b = stream == null ? -1 : stream.read();
		if (b == -1) {
			throw new IOException("Unexpected end of the stream.");
		}
		return b;
	}

	/**
	 */
	@Override public synchronized void close() throws IOException {
		if (!closed.get()) {
			this.socket.close();
			this.stream = null;
			this.closed.set(true);
		}
	}

	/**
	 * Asserts that this stream is opened. If not an exception is thrown.
	 *
	 * @throws IOException If this stream has been already closed.
	 */
	private void assertOpenedOrThrowException() throws IOException {
		if (closed.get()) throw new IOException(TAG + " has been already closed.");
	}

	/**
	 * Destroys this stream. Destroying the stream also marks it as closed.
	 */
	void destroy() {
		this.stream = null;
		if (countDownLatch != null) {
			this.countDownLatch.countDown();
			this.countDownLatch = null;
		}
		this.delegate.unregisterOnIncomingFrameListener(this);
		this.closed.set(true);
	}

	/*
	 * Inner classes ===============================================================================
	 */
}