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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;

/**
 * A {@link Socket} implementation which may be used in order to adapt an asynchronous nature of
 * a concrete WebSocket implementation into synchronous nature of raw Socket. WebSocketAdapter
 * requires a concrete WebSocket implementation to be represented by {@link WebSocketDelegate}
 * interface with which instance may be web socket adapter instantiated via
 * {@link #WebSocketAdapter(WebSocketDelegate)} constructor. The adapter is then delegating all
 * appropriate calls to the delegate instance, like sending of frames, and is also listening for
 * the appropriate callbacks, like receiving frames.
 *
 * @author Martin Albedinsky
 * @since 1.0
 */
public class WebSocketAdapter extends Socket {

	/*
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "WebSocketAdapter";

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
	 * Delegate that hides concrete implementation of WebSocket that this adapter adapts in context
	 * of synchronous nature of Socket.
	 */
	private final WebSocketDelegate delegate;

	/**
	 * Boolean flag indicating whether this socket is connected to the remote server at this time
	 * or not.
	 */
	@SuppressWarnings("WeakerAccess")
	final AtomicBoolean connected = new AtomicBoolean(false);

	/**
	 * Stream used as store data that are received from the WebSocket.
	 */
	private WebSocketInputStream inputStream;

	/**
	 * Stream used to store data to be send/delegated to the WebSocket.
	 */
	private WebSocketOutputStream outputStream;

	/*
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of WebSocketAdapter with the specified WebSocket delegate to be adapted
	 * into synchronous nature of {@link Socket}.
	 *
	 * @param delegate The delegate that hides a concrete implementation of a WebSocket. The adapter
	 *                 will delegate all appropriate calls to the delegate and also will listen for
	 *                 all appropriate callbacks to properly satisfy Socket implementation.
	 */
	public WebSocketAdapter(@Nonnull final WebSocketDelegate delegate) {
		super();
		this.delegate = delegate;
		this.delegate.registerOnConnectionListener(new WebSocketDelegate.OnConnectionListener() {

			/**
			 */
			@Override public void onConnected() {
				connected.set(true);
			}

			/**
			 */
			@Override public void onDisconnected() {
				connected.set(false);
				destroyStreams();
			}
		});
	}

	/*
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override public void connect(@Nonnull final SocketAddress endpoint) throws IOException {
		connect(endpoint, 0);
	}

	/**
	 */
	@Override public void connect(@Nonnull final SocketAddress endpoint, final int timeout) throws IOException {
		this.delegate.connect(endpoint, timeout);
	}

	/**
	 * Checks whether the adapted WebSocket is connected to the remote server.
	 *
	 * @return {@code True} if the WebSocket is connected to the remote server at this time,
	 * {@code false} otherwise.
	 *
	 * @see #isClosed()
	 */
	@Override public final boolean isConnected() {
		return delegate.isConnected();
	}

	/**
	 * Asserts that this socket is connected. If not an exception is thrown.
	 *
	 * @throws IOException If this socket is not connected.
	 *
	 * @see #isConnected()
	 */
	protected final void assertConnectedOrThrowException() throws IOException {
		if (!isConnected()) throw new IOException("Already disconnected.");
	}

	/**
	 */
	@Override public synchronized final InputStream getInputStream() throws IOException {
		assertOpenedOrThrowException();
		assertConnectedOrThrowException();
		if (inputStream == null) {
			this.inputStream = new WebSocketInputStream(this, delegate);
		}
		return inputStream;
	}

	/**
	 */
	@Override public synchronized final OutputStream getOutputStream() throws IOException {
		assertOpenedOrThrowException();
		assertConnectedOrThrowException();
		if (outputStream == null) {
			this.outputStream = new WebSocketOutputStream(this, delegate);
		}
		return outputStream;
	}

	/**
	 * Asserts that this socket is opened. If not an exception is thrown.
	 *
	 * @throws IOException If this socket is not opened.
	 *
	 * @see #isClosed()
	 */
	protected final void assertOpenedOrThrowException() throws IOException {
		if (isClosed()) throw new IOException("Already closed.");
	}

	/**
	 */
	@Override public synchronized final void close() throws IOException {
		if (!isClosed()) {
			delegate.close();
			destroyStreams();
		}
	}

	/**
	 * Destroys both, input and output, streams of this WebSocket adapter.
	 */
	@SuppressWarnings("WeakerAccess")
	void destroyStreams() {
		if (inputStream != null) {
			this.inputStream.destroy();
			this.inputStream = null;
		}
		if (outputStream != null) {
			this.outputStream.destroy();
			this.outputStream = null;
		}
	}

	/**
	 * Checks whether the adapted WebSocket is closed.
	 *
	 * @return {@code True} if the WebSocket is already closed, {@code false} otherwise.
	 *
	 * @see #isConnected()
	 */
	@Override public final boolean isClosed() {
		return delegate.isClosed();
	}

	/*
	 * Inner classes ===============================================================================
	 */
}