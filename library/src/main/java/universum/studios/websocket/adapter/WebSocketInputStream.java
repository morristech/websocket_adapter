/*
 * =================================================================================================
 *                             Copyright (C) 2017 Universum Studios
 * =================================================================================================
 *         Licensed under the Apache License, Version 2.0 or later (further "License" only).
 * -------------------------------------------------------------------------------------------------
 * You may use this file only in compliance with the License. More details and copy of this License
 * you may obtain at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * You can redistribute, modify or publish any part of the code written within this file but as it
 * is described in the License, the software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES or CONDITIONS OF ANY KIND.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 * =================================================================================================
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
	 * Initial size for queue storing received data.
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
	private final AtomicBoolean mClosed = new AtomicBoolean(false);

	/**
	 * Parent socket using this stream as its input.
	 */
	private final Closeable mSocket;

	/**
	 * Delegate from which is this stream receiving all frame's payload data and making them available
	 * via its read methods.
	 */
	private final WebSocketDelegate mDelegate;

	/**
	 * Buffer storing all data from the received frames via {@link #onFrameReceived(WebSocketDelegate.Frame)}
	 * which were not marked as final frames.
	 *
	 * @see WebSocketDelegate.Frame#isFinal()
	 */
	private byte[] mDataBuffer = new byte[0];

	/**
	 * Queue used to store payload data of the received frames which are processed/read one at a time.
	 *
	 * @see WebSocketDelegate.Frame#getPayload()
	 */
	private final Queue<byte[]> mDataQueue;

	/**
	 * Lock used for synchronized operations upon {@link #mDataQueue}.
	 */
	private final Object mDataLock = new Object();

	/**
	 * Internal stream used for reading of received data.
	 */
	private ByteArrayInputStream mStream;

	/**
	 * Count down latch that is used to lock reading of this stream's data until there are some
	 * new data to be read.
	 */
	private CountDownLatch mCountDownLatch;

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
		this.mSocket = socket;
		this.mDelegate = delegate;
		this.mDelegate.registerOnIncomingFrameListener(this);
		this.mDataQueue = new LinkedBlockingQueue<>(DATA_QUEUE_INITIAL_SIZE);
	}

	/*
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override
	public void onFrameReceived(@Nonnull final WebSocketDelegate.Frame frame) {
		if (mClosed.get()) {
			return;
		}
		synchronized (mDataLock) {
			final byte[] payload = frame.getPayload();
			// Expand the existing buffer and write the payload data into it.
			final byte[] newBuffer = new byte[mDataBuffer.length + payload.length];
			System.arraycopy(mDataBuffer, 0, newBuffer, 0, mDataBuffer.length);
			System.arraycopy(payload, 0, newBuffer, mDataBuffer.length, payload.length);
			this.mDataBuffer = newBuffer;
			if (frame.isFinal() && mDataBuffer.length > 0) {
				addData(mDataBuffer);
				this.mDataBuffer = new byte[0];
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
		if (mStream == null) {
			this.mStream = new ByteArrayInputStream(bytes);
			if (mCountDownLatch != null && mCountDownLatch.getCount() > 0) {
				this.mCountDownLatch.countDown();
			}
			this.mCountDownLatch = null;
		} else {
			mDataQueue.add(bytes);
		}
	}

	/**
	 */
	@Override
	public synchronized int available() throws IOException {
		assertOpenedOrThrowException();
		return mStream == null ? 0 : mStream.available();
	}


	/**
	 */
	@Override
	public synchronized long skip(final long n) throws IOException {
		assertOpenedOrThrowException();
		return mStream == null ? 0 : mStream.skip(n);
	}

	/**
	 */
	@Override
	public boolean markSupported() {
		return true;
	}

	/**
	 */
	@Override
	public synchronized void mark(final int i) {
		if (mStream != null) mStream.mark(i);
	}

	/**
	 */
	@Override
	public synchronized void reset() throws IOException {
		assertOpenedOrThrowException();
		this.mStream = null;
	}

	/**
	 */
	@Override
	public synchronized int read() throws IOException {
		assertOpenedOrThrowException();
		if (mStream != null) {
			final int b = mStream.read();
			if (b == -1) {
				this.mStream = null;
			}
			return b;
		}
		// No more data in the stream, check if we have some queued data.
		synchronized (mDataLock) {
			if (mDataQueue.size() > 0) {
				this.mStream = new ByteArrayInputStream(mDataQueue.poll());
			} else {
				this.mStream = null;
				this.mCountDownLatch = new CountDownLatch(1);
			}
		}
		if (mStream == null) {
			// We do not have any data to read from, wait for the new one.
			try {
				this.mCountDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		final int b = mStream.read();
		if (b == -1) {
			throw new IOException("Unexpected end of the data.");
		}
		return b;
	}


	/**
	 */
	@Override
	public synchronized void close() throws IOException {
		if (!mClosed.get()) {
			this.mSocket.close();
			this.mClosed.set(true);
		}
	}

	/**
	 * Asserts that this stream is opened. If not an exception is thrown.
	 *
	 * @throws IOException If this stream has been already closed.
	 */
	private void assertOpenedOrThrowException() throws IOException {
		if (mClosed.get()) throw new IOException(TAG + " has been already closed.");
	}

	/**
	 * Destroys this stream. Destroying the stream also marks it as closed.
	 */
	void destroy() {
		this.mStream = null;
		if (mCountDownLatch != null) {
			this.mCountDownLatch.countDown();
			this.mCountDownLatch = null;
		}
		this.mDelegate.unregisterOnIncomingFrameListener(this);
		this.mClosed.set(true);
	}

	/*
	 * Inner classes ===============================================================================
	 */
}
