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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * @author Martin Albedinsky
 */
public class WebSocketAdapter extends Socket {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * todo:
	 */
	private final WebSocketDelegate mWebSocketDelegate;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * todo:
	 *
	 * @param facade
	 */
	public WebSocketAdapter(@NotNull WebSocketDelegate facade) {
		this.mWebSocketDelegate = facade;
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override
	public void connect(SocketAddress endpoint) throws IOException {
		super.connect(endpoint);
	}

	/**
	 */
	@Override
	public void connect(SocketAddress endpoint, int timeout) throws IOException {
		super.connect(endpoint, timeout);
	}

	/**
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return super.getInputStream();
	}

	/**
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		return super.getOutputStream();
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
