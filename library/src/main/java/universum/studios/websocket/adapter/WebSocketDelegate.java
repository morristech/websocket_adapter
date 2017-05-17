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

import java.io.IOException;
import java.net.SocketAddress;

import javax.annotation.Nonnull;

/**
 * An interface that is used to hide a concrete implementation of WebSocket for purpose of adapting
 * an asynchronous nature of WebSocket to synchronous nature of standard {@link java.net.Socket Socket}
 * using {@link WebSocketAdapter}.
 *
 * @author Martin Albedinsky
 */
public interface WebSocketDelegate {

	/**
	 * Listener which may be used to listen for callbacks about connected and disconnected WebSocket.
	 *
	 * @author Martin Albedinsky
	 */
	interface OnConnectionListener {

		/**
		 * Invoked whenever the WebSocket becomes connected.
		 */
		void onConnected();

		/**
		 * Invoked whenever the WebSocket becomes disconnected.
		 */
		void onDisconnected();
	}

	/**
	 * Basic interface for WebSocket frame.
	 */
	interface Frame {

		/**
		 * Implementation of {@link Frame} which provides empty payload.
		 */
		Frame EMPTY = new Frame() {

			/**
			 */
			@Nonnull
			@Override
			public byte[] getPayload() {
				return new byte[0];
			}

			/**
			 */
			@Override
			public boolean isFinal() {
				return true;
			}
		};

		/**
		 * Returns the payload data of this frame.
		 *
		 * @return Frame's payload.
		 */
		@Nonnull
		byte[] getPayload();

		/**
		 * Returns boolean flag indicating whether this frame is a final frame.
		 *
		 * @return {@code True} if this frame is a final one, {@code false} if there are more frames
		 * to come to properly receive all the data.
		 */
		boolean isFinal();
	}

	/**
	 * Listener which may be used to listen for callback about received WebSocket frame.
	 *
	 * @author Martin Albedinsky
	 */
	interface OnIncomingFrameListener {

		/**
		 * Invoked whenever a new frame is received by the WebSocket.
		 *
		 * @param frame The received frame.
		 */
		void onFrameReceived(@Nonnull Frame frame);
	}

	/**
	 * Registers a listener to be invoked whenever the WebSocket connects or disconnects.
	 *
	 * @param listener The desired listener to be registered.
	 * @see #unregisterOnConnectionListener(OnConnectionListener)
	 */
	void registerOnConnectionListener(@Nonnull OnConnectionListener listener);

	/**
	 * Un-registers the specified <var>listener</var> from the registered ones, so it will no longer
	 * receive any callbacks.
	 *
	 * @param listener The desired listener to be un-registered.
	 * @see #registerOnConnectionListener(OnConnectionListener)
	 */
	void unregisterOnConnectionListener(@Nonnull OnConnectionListener listener);

	/**
	 * Registers a listener to be invoked whenever the WebSocket receives a new frame.
	 *
	 * @param listener The desired listener to be registered.
	 * @see #unregisterOnIncomingFrameListener(OnIncomingFrameListener)
	 */
	void registerOnIncomingFrameListener(@Nonnull OnIncomingFrameListener listener);

	/**
	 * Un-registers the specified <var>listener</var> from the registered ones, so it will no longer
	 * receive any callbacks.
	 *
	 * @param listener The desired listener to be un-registered.
	 * @see #registerOnIncomingFrameListener(OnIncomingFrameListener)
	 */
	void unregisterOnIncomingFrameListener(@Nonnull OnIncomingFrameListener listener);

	/**
	 * Performs connection of the wrapped WebSocket to the specified <var>remoteAddress</var>.
	 *
	 * @param remoteAddress The remote address to which should the WebSocket connect.
	 * @param timeout       The timeout for the connection.
	 * @throws IOException If some IO error occurs during connection attempt.
	 */
	void connect(@Nonnull SocketAddress remoteAddress, int timeout) throws IOException;

	/**
	 * Sends the specified <var>frame</var> via the wrapped WebSocket.
	 *
	 * @param frame The frame to be send.
	 * @throws IOException If some IO error occurs during send operation or if the wrapped WebSocket
	 *                     is already closed.
	 */
	void sendFrame(@Nonnull Frame frame) throws IOException;

	/**
	 * Closes the wrapped WebSocket.
	 *
	 * @throws IOException If some IO error occurs during close operation.
	 * @see #isClosed()
	 */
	void close() throws IOException;

	/**
	 * Returns boolean flag indicating whether the wrapped WebSocket is closed. The WebSocket should
	 * be closed always after {@link #close()} has been called.
	 *
	 * @return {@code True} if WebSocket is already closed, {@code false} otherwise.
	 */
	boolean isClosed();
}
