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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * A {@link WebSocketDelegate} implementation which is recommended to be used as base for all
 * concrete WebSocketDelegate implementations.
 * <p>
 * This base class implements mainly logic for registration and un-registration of
 * {@link OnConnectionListener OnConnectionListenera} and {@link OnIncomingFrameListener OnIncomingFrameListeners}.
 *
 * @author Martin Albedinsky
 */
public abstract class BaseWebSocketDelegate implements WebSocketDelegate {

    /*
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "BaseWebSocketDelegate";

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
	 * List containing all {@link OnConnectionListener} that has been registered via
	 * {@link #registerOnConnectionListener(OnConnectionListener)}.
	 */
	private final List<OnConnectionListener> mConnectionListeners = new ArrayList<>(1);

	/**
	 * List containing all {@link OnIncomingFrameListener} that has been registered via
	 * {@link #registerOnIncomingFrameListener(OnIncomingFrameListener)}.
	 */
	private final List<OnIncomingFrameListener> mIncomingFrameListeners = new ArrayList<>(1);
	 
	/*
	 * Constructors ================================================================================
	 */
	 
	/*
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override
	public void registerOnConnectionListener(@Nonnull final OnConnectionListener listener) {
		synchronized (mConnectionListeners) {
			if (!mConnectionListeners.contains(listener)) mConnectionListeners.add(listener);
		}
	}

	/**
	 * Notifies all registered {@link OnConnectionListener OnConnectionListeners} that the adapted
	 * WebSocket has been connected.
	 */
	protected void notifyConnected() {
		synchronized (mConnectionListeners) {
			if (!mConnectionListeners.isEmpty()) {
				for (final OnConnectionListener listener : mConnectionListeners) {
					listener.onConnected();
				}
			}
		}
	}

	/**
	 * Notifies all registered {@link OnConnectionListener OnConnectionListeners} that the adapted
	 * WebSocket has been disconnected.
	 */
	protected void notifyDisconnected() {
		synchronized (mConnectionListeners) {
			if (!mConnectionListeners.isEmpty()) {
				for (final OnConnectionListener listener : mConnectionListeners) {
					listener.onDisconnected();
				}
			}
		}
	}

	/**
	 */
	@Override
	public void unregisterOnConnectionListener(@Nonnull final OnConnectionListener listener) {
		synchronized (mConnectionListeners) {
			mConnectionListeners.remove(listener);
		}
	}

	/**
	 */
	@Override
	public void registerOnIncomingFrameListener(@Nonnull final OnIncomingFrameListener listener) {
		synchronized (mIncomingFrameListeners) {
			if (!mIncomingFrameListeners.contains(listener)) mIncomingFrameListeners.add(listener);
		}
	}

	/**
	 * Notifies all registered {@link OnIncomingFrameListener OnIncomingFrameListeners} about received
	 * <var>frame</var>.
	 *
	 * @param frame The frame that has been received by the adapted WebSocket.
	 */
	protected void notifyFrameReceived(@Nonnull final Frame frame) {
		synchronized (mIncomingFrameListeners) {
			if (!mIncomingFrameListeners.isEmpty()) {
				for (final OnIncomingFrameListener watcher : mIncomingFrameListeners) {
					watcher.onFrameReceived(frame);
				}
			}
		}
	}

	/**
	 */
	@Override
	public void unregisterOnIncomingFrameListener(@Nonnull final OnIncomingFrameListener listener) {
		synchronized (mIncomingFrameListeners) {
			mIncomingFrameListeners.remove(listener);
		}
	}

	/*
	 * Inner classes ===============================================================================
	 */
}
