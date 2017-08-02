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
package universum.studios.samples.websocket.adapter;

import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketCloseCode;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import universum.studios.websocket.adapter.BaseWebSocketDelegate;

/**
 * @author Martin Albedinsky
 */
public final class SampleWebSocket extends universum.studios.websocket.adapter.WebSocketAdapter {

	@SuppressWarnings("unused")
	private static final String TAG = "SampleWebSocket";

	public SampleWebSocket() {
		super(new SocketDelegate());
	}

	private static final class SocketDelegate extends BaseWebSocketDelegate {

		private static final String URL_FORMAT = "wss://%s:%d";

		private final WebSocketListener LISTENER = new WebSocketAdapter() {

			@Override
			public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
				notifyConnected();
			}

			@Override
			public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
				notifyFrameReceived(new universum.studios.websocket.adapter.WebSocketFrame.Builder()
						.payload(frame.getPayload())
						.isFinal(frame.getFin())
						.build()
				);
			}

			@Override
			public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
				close();
			}

			@Override
			public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
				webSocket = null;
				notifyDisconnected();
			}
		};

		final WebSocketFactory factory;
		WebSocket webSocket;

		SocketDelegate() {
			this.factory = new WebSocketFactory();
		}

		@Override
		public void connect(@Nonnull SocketAddress remoteAddress, int timeout) throws IOException {
			final InetSocketAddress address = assertSupportedAddressOrThrow(remoteAddress);
			this.webSocket = connectTo(String.format(
					URL_FORMAT,
					address.getHostName(),
					address.getPort()),
					timeout
			);
		}

		private static InetSocketAddress assertSupportedAddressOrThrow(SocketAddress address) {
			if (!(address instanceof InetSocketAddress)) {
				throw new IllegalArgumentException("Not supported socket address!");
			}
			return (InetSocketAddress) address;
		}

		private WebSocket connectTo(String url, long timeout) {
			try {
				final WebSocket webSocket = factory.createSocket(url, (int) timeout);
				webSocket.addListener(LISTENER);
				webSocket.connect();
				return webSocket;
			} catch (IOException e) {
				System.out.println("Connection failed due to IO failure!");
			} catch (OpeningHandshakeException e) {
				System.out.println("Connection handshake failed due to(" + e.getStatusLine() + ")!");
			} catch (Exception e) {
				System.out.println("Connection failed!");
			}
			return null;
		}


		@Override
		public boolean isConnected() {
			return webSocket != null && webSocket.isOpen();
		}

		@Override
		public void sendFrame(@Nonnull Frame frame) throws IOException {
			if (webSocket != null) webSocket.sendBinary(frame.getPayload());
		}

		@Override
		public void close() throws IOException {
			if (webSocket.isOpen()) webSocket.disconnect(WebSocketCloseCode.NORMAL, null, 0);
		}

		@Override
		public boolean isClosed() {
			return webSocket == null || !webSocket.isOpen();
		}
	}
}
