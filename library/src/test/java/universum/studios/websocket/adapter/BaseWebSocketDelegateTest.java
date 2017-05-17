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

import org.junit.Test;

import java.io.IOException;
import java.net.SocketAddress;

import javax.annotation.Nonnull;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Martin Albedinsky
 */
public final class BaseWebSocketDelegateTest {
    
	@SuppressWarnings("unused")
	private static final String TAG = "BaseWebSocketDelegateTest";

    @Test
	public void testRegisterOnConnectionListener() {
	    final BaseWebSocketDelegate delegate = new TestDelegate();
	    final WebSocketDelegate.OnConnectionListener firstMockListener = mock(WebSocketDelegate.OnConnectionListener.class);
	    final WebSocketDelegate.OnConnectionListener secondMockListener = mock(WebSocketDelegate.OnConnectionListener.class);
	    delegate.registerOnConnectionListener(firstMockListener);
	    delegate.registerOnConnectionListener(firstMockListener);
	    delegate.registerOnConnectionListener(secondMockListener);
	    delegate.notifyConnected();
	    verify(firstMockListener, times(1)).onConnected();
	    verify(secondMockListener, times(1)).onConnected();
	    verifyNoMoreInteractions(firstMockListener);
	    verifyNoMoreInteractions(secondMockListener);
	}

    @Test
	public void testUnregisterOnConnectionListener() {
	    final BaseWebSocketDelegate delegate = new TestDelegate();
	    final WebSocketDelegate.OnConnectionListener firstMockListener = mock(WebSocketDelegate.OnConnectionListener.class);
	    final WebSocketDelegate.OnConnectionListener secondMockListener = mock(WebSocketDelegate.OnConnectionListener.class);
	    delegate.registerOnConnectionListener(firstMockListener);
	    delegate.registerOnConnectionListener(secondMockListener);
	    delegate.unregisterOnConnectionListener(firstMockListener);
	    delegate.notifyConnected();
	    verifyZeroInteractions(firstMockListener);
	    verify(secondMockListener, times(1)).onConnected();
	    verifyNoMoreInteractions(secondMockListener);
	}

    @Test
	public void testUnregisterOnConnectionListenerWithoutRegisteredListeners() {
	    // Only ensure that the delegate does cause any troubles.
	    new TestDelegate().unregisterOnConnectionListener(mock(WebSocketDelegate.OnConnectionListener.class));
	}

    @Test
	public void testNotifyConnected() {
	    final BaseWebSocketDelegate delegate = new TestDelegate();
	    final WebSocketDelegate.OnConnectionListener mockListener = mock(WebSocketDelegate.OnConnectionListener.class);
	    delegate.registerOnConnectionListener(mockListener);
	    delegate.notifyConnected();
	    verify(mockListener, times(1)).onConnected();
	    verifyNoMoreInteractions(mockListener);
	}

    @Test
	public void testNotifyConnectedWithoutRegisteredListeners() {
	    // Only ensure that the delegate does cause any troubles.
	    new TestDelegate().notifyConnected();
	}

    @Test
	public void testNotifyDisconnected() {
	    final BaseWebSocketDelegate delegate = new TestDelegate();
	    final WebSocketDelegate.OnConnectionListener mockListener = mock(WebSocketDelegate.OnConnectionListener.class);
	    delegate.registerOnConnectionListener(mockListener);
	    delegate.notifyDisconnected();
	    verify(mockListener, times(1)).onDisconnected();
	    verifyNoMoreInteractions(mockListener);
    }

    @Test
	public void testNotifyDisconnectedWithoutRegisteredListeners() {
	    // Only ensure that the delegate does cause any troubles.
	    new TestDelegate().notifyDisconnected();
	}

    @Test
	public void testRegisterOnIncomingFrameListener() {
	    final BaseWebSocketDelegate delegate = new TestDelegate();
	    final WebSocketDelegate.OnIncomingFrameListener firstMockListener = mock(WebSocketDelegate.OnIncomingFrameListener.class);
	    final WebSocketDelegate.OnIncomingFrameListener secondMockListener = mock(WebSocketDelegate.OnIncomingFrameListener.class);
	    delegate.registerOnIncomingFrameListener(firstMockListener);
	    delegate.registerOnIncomingFrameListener(firstMockListener);
	    delegate.registerOnIncomingFrameListener(secondMockListener);
	    final WebSocketDelegate.Frame mockFrame = mock(WebSocketDelegate.Frame.class);
	    delegate.notifyFrameReceived(mockFrame);
	    verify(firstMockListener, times(1)).onFrameReceived(mockFrame);
	    verify(secondMockListener, times(1)).onFrameReceived(mockFrame);
	    verifyNoMoreInteractions(firstMockListener);
	    verifyNoMoreInteractions(secondMockListener);
	}

    @Test
	public void testUnregisterOnIncomingFrameListener() {
	    final BaseWebSocketDelegate delegate = new TestDelegate();
	    final WebSocketDelegate.OnIncomingFrameListener firstMockListener = mock(WebSocketDelegate.OnIncomingFrameListener.class);
	    final WebSocketDelegate.OnIncomingFrameListener secondMockListener = mock(WebSocketDelegate.OnIncomingFrameListener.class);
	    delegate.registerOnIncomingFrameListener(firstMockListener);
	    delegate.registerOnIncomingFrameListener(secondMockListener);
	    delegate.unregisterOnIncomingFrameListener(firstMockListener);
	    final WebSocketDelegate.Frame mockFrame = mock(WebSocketDelegate.Frame.class);
	    delegate.notifyFrameReceived(mockFrame);
	    verifyZeroInteractions(firstMockListener);
	    verify(secondMockListener, times(1)).onFrameReceived(mockFrame);
	    verifyNoMoreInteractions(secondMockListener);
	}

    @Test
	public void testUnregisterOnIncomingFrameListenerWithoutRegisteredListeners() {
	    // Only ensure that the delegate does cause any troubles.
	    new TestDelegate().unregisterOnIncomingFrameListener(mock(WebSocketDelegate.OnIncomingFrameListener.class));
	}

    @Test
	public void testNotifyFrameReceived() {
	    final BaseWebSocketDelegate delegate = new TestDelegate();
	    final WebSocketDelegate.OnIncomingFrameListener mockListener = mock(WebSocketDelegate.OnIncomingFrameListener.class);
		delegate.registerOnIncomingFrameListener(mockListener);
	    final WebSocketDelegate.Frame mockFrame = mock(WebSocketDelegate.Frame.class);
	    delegate.notifyFrameReceived(mockFrame);
	    verify(mockListener, times(1)).onFrameReceived(mockFrame);
	    verifyNoMoreInteractions(mockListener);
	}

    @Test
	public void testNotifyReceiveFrameWithoutRegisteredListeners() {
	    // Only ensure that the delegate does cause any troubles.
	    new TestDelegate().notifyFrameReceived(mock(WebSocketDelegate.Frame.class));
	}

	private static class TestDelegate extends BaseWebSocketDelegate {

		@Override
		public void connect(@Nonnull SocketAddress remoteAddress, int timeout) throws IOException {
		}

		@Override
		public void sendFrame(@Nonnull Frame frame) throws IOException {
		}

		@Override
		public void close() throws IOException {
		}

		@Override
		public boolean isClosed() {
			return false;
		}
	}
}
