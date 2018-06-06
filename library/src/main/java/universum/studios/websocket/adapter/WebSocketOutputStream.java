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
 * @since 1.0
 *
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
	private final Closeable socket;

	/**
	 * Delegate to which is this stream sending all written data whenever they are requested to be
	 * flushed via {@link #flush()}.
	 */
	private final WebSocketDelegate delegate;

	/**
	 * Boolean flag indicating whether this stream is closed or not.
	 */
	private final AtomicBoolean closed = new AtomicBoolean(false);

	/**
	 * Internal stream used to store written data.
	 */
	private ByteArrayOutputStream stream;

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
		super();
		this.socket = socket;
		this.delegate = delegate;
		this.stream = new ByteArrayOutputStream(BUFFER_INITIAL_SIZE);
	}

	/*
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override public void write(@Nonnull final byte[] bytes, final int offset, final int length) throws IOException {
		assertOpenedOrThrowException();
		this.stream.write(bytes, offset, length);
	}

	/**
	 */
	@Override public void write(final int b) throws IOException {
		assertOpenedOrThrowException();
		this.stream.write(b);
	}

	/**
	 */
	@Override public synchronized void flush() throws IOException {
		assertOpenedOrThrowException();
		if (stream.size() > 0) {
			this.delegate.sendFrame(new WebSocketFrame.Builder().payload(stream.toByteArray()).build());
			this.stream.reset();
		}
	}

	/**
	 */
	@Override public synchronized void close() throws IOException {
		if (!closed.get()) {
			this.stream.reset();
			this.socket.close();
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
		this.closed.set(true);
	}

	/*
	 * Inner classes ===============================================================================
	 */
}