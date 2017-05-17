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

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;

/**
 * An {@link OutputStream} implementation used by {@link WebSocketAdapter} as its output stream.
 *
 * @author Martin Albedinsky
 * @see WebSocketInputStream
 */
final class WebSocketOutputStream extends OutputStream {

	/*
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	@SuppressWarnings("unused")
	private static final String TAG = "WebSocketOutputStream";

	/**
	 * Initial size for the buffer used by internal data stream.
	 */
	private static final int BUFFER_INITIAL_SIZE = 1024;

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
	 * Parent socket using this stream as its output.
	 */
	private final Closeable mSocket;

	/**
	 * Delegate to which is this stream sending all written data whenever they are requested to be
	 * flushed via {@link #flush()}.
	 */
	private final WebSocketDelegate mDelegate;

	/**
	 * Boolean flag indicating whether this stream is closed or not.
	 */
	private final AtomicBoolean mClosed = new AtomicBoolean(false);

	/**
	 * Internal stream used to store written data.
	 */
	private ByteArrayOutputStream mStream;

	/*
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of WebSocketOutputStream for the given <var>socket</var> and <var>delegate</var>.
	 *
	 * @param socket   The parent socket that will use this stream as its output.
	 * @param delegate The delegate to which should this stream send data whenever a new data are
	 *                 written to the stream and requested to be flushed via {@link #flush()}.
	 */
	WebSocketOutputStream(final Closeable socket, final WebSocketDelegate delegate) {
		this.mSocket = socket;
		this.mDelegate = delegate;
		this.mStream = new ByteArrayOutputStream(BUFFER_INITIAL_SIZE);
	}

	/*
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override
	public void write(@Nonnull final byte[] bytes, final int offset, final int length) throws IOException {
		assertOpenedOrThrowException();
		this.mStream.write(bytes, offset, length);
	}

	/**
	 */
	@Override
	public void write(final int b) throws IOException {
		assertOpenedOrThrowException();
		this.mStream.write(b);
	}

	/**
	 */
	@Override
	public synchronized void flush() throws IOException {
		assertOpenedOrThrowException();
		if (mStream.size() > 0) {
			this.mDelegate.sendFrame(new WebSocketFrame.Builder().payload(mStream.toByteArray()).build());
			this.mStream.reset();
		}
	}

	/**
	 */
	@Override
	public synchronized void close() throws IOException {
		if (!mClosed.get()) {
			this.mStream.reset();
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
		this.mClosed.set(true);
	}

	/*
	 * Inner classes ===============================================================================
	 */
}
