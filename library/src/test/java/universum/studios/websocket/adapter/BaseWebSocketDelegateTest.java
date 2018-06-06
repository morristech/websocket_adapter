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

import org.junit.Test;

import java.io.IOException;
import java.net.SocketAddress;

import javax.annotation.Nonnull;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Martin Albedinsky
 */
public final class BaseWebSocketDelegateTest {
    
    @Test public void testRegisterOnConnectionListener() {
    	// Arrange:
	    final BaseWebSocketDelegate delegate = new TestDelegate();
	    final WebSocketDelegate.OnConnectionListener firstMockListener = mock(WebSocketDelegate.OnConnectionListener.class);
	    final WebSocketDelegate.OnConnectionListener secondMockListener = mock(WebSocketDelegate.OnConnectionListener.class);
	    // Act:
	    delegate.registerOnConnectionListener(firstMockListener);
	    delegate.registerOnConnectionListener(firstMockListener);
	    delegate.registerOnConnectionListener(secondMockListener);
	    // Assert:
	    delegate.notifyConnected();
	    verify(firstMockListener).onConnected();
	    verify(secondMockListener).onConnected();
	    verifyNoMoreInteractions(firstMockListener);
	    verifyNoMoreInteractions(secondMockListener);
	}

    @Test public void testUnregisterOnConnectionListener() {
	    // Arrange:
    	final BaseWebSocketDelegate delegate = new TestDelegate();
	    final WebSocketDelegate.OnConnectionListener firstMockListener = mock(WebSocketDelegate.OnConnectionListener.class);
	    final WebSocketDelegate.OnConnectionListener secondMockListener = mock(WebSocketDelegate.OnConnectionListener.class);
	    delegate.registerOnConnectionListener(firstMockListener);
	    delegate.registerOnConnectionListener(secondMockListener);
	    // Act:
	    delegate.unregisterOnConnectionListener(firstMockListener);
	    // Assert:
	    delegate.notifyConnected();
	    verifyZeroInteractions(firstMockListener);
	    verify(secondMockListener).onConnected();
	    verifyNoMoreInteractions(secondMockListener);
	}

    @Test public void testUnregisterOnConnectionListenerWithoutRegisteredListeners() {
	    // Act:
	    // Only ensure that the delegate does cause any troubles.
	    new TestDelegate().unregisterOnConnectionListener(mock(WebSocketDelegate.OnConnectionListener.class));
	}

    @Test public void testNotifyConnected() {
	    // Arrange:
    	final BaseWebSocketDelegate delegate = new TestDelegate();
	    final WebSocketDelegate.OnConnectionListener mockListener = mock(WebSocketDelegate.OnConnectionListener.class);
	    delegate.registerOnConnectionListener(mockListener);
	    // Act:
	    delegate.notifyConnected();
	    // Assert:
	    verify(mockListener).onConnected();
	    verifyNoMoreInteractions(mockListener);
	}

    @Test public void testNotifyConnectedWithoutRegisteredListeners() {
	    // Act:
    	// Only ensure that the delegate does cause any troubles.
	    new TestDelegate().notifyConnected();
	}

    @Test public void testNotifyDisconnected() {
	    // Arrange:
    	final BaseWebSocketDelegate delegate = new TestDelegate();
	    final WebSocketDelegate.OnConnectionListener mockListener = mock(WebSocketDelegate.OnConnectionListener.class);
	    delegate.registerOnConnectionListener(mockListener);
	    // Act:
	    delegate.notifyDisconnected();
	    // Assert:
	    verify(mockListener).onDisconnected();
	    verifyNoMoreInteractions(mockListener);
    }

    @Test public void testNotifyDisconnectedWithoutRegisteredListeners() {
	    // Act:
    	// Only ensure that the delegate does cause any troubles.
	    new TestDelegate().notifyDisconnected();
	}

    @Test public void testRegisterOnIncomingFrameListener() {
	    // Arrange:
    	final BaseWebSocketDelegate delegate = new TestDelegate();
	    final WebSocketDelegate.OnIncomingFrameListener firstMockListener = mock(WebSocketDelegate.OnIncomingFrameListener.class);
	    final WebSocketDelegate.OnIncomingFrameListener secondMockListener = mock(WebSocketDelegate.OnIncomingFrameListener.class);
	    // Act:
	    delegate.registerOnIncomingFrameListener(firstMockListener);
	    delegate.registerOnIncomingFrameListener(firstMockListener);
	    delegate.registerOnIncomingFrameListener(secondMockListener);
	    final WebSocketDelegate.Frame mockFrame = mock(WebSocketDelegate.Frame.class);
	    // Assert:
	    delegate.notifyFrameReceived(mockFrame);
	    verify(firstMockListener).onFrameReceived(mockFrame);
	    verify(secondMockListener).onFrameReceived(mockFrame);
	    verifyNoMoreInteractions(firstMockListener);
	    verifyNoMoreInteractions(secondMockListener);
	}

    @Test public void testUnregisterOnIncomingFrameListener() {
	    // Arrange:
    	final BaseWebSocketDelegate delegate = new TestDelegate();
	    final WebSocketDelegate.OnIncomingFrameListener firstMockListener = mock(WebSocketDelegate.OnIncomingFrameListener.class);
	    final WebSocketDelegate.OnIncomingFrameListener secondMockListener = mock(WebSocketDelegate.OnIncomingFrameListener.class);
	    delegate.registerOnIncomingFrameListener(firstMockListener);
	    delegate.registerOnIncomingFrameListener(secondMockListener);
	    // Act:
	    delegate.unregisterOnIncomingFrameListener(firstMockListener);
	    // Assert:
	    final WebSocketDelegate.Frame mockFrame = mock(WebSocketDelegate.Frame.class);
	    delegate.notifyFrameReceived(mockFrame);
	    verifyZeroInteractions(firstMockListener);
	    verify(secondMockListener).onFrameReceived(mockFrame);
	    verifyNoMoreInteractions(secondMockListener);
	}

    @Test public void testUnregisterOnIncomingFrameListenerWithoutRegisteredListeners() {
	    // Act:
    	// Only ensure that the delegate does cause any troubles.
	    new TestDelegate().unregisterOnIncomingFrameListener(mock(WebSocketDelegate.OnIncomingFrameListener.class));
	}

    @Test public void testNotifyFrameReceived() {
	    // Arrange:
    	final BaseWebSocketDelegate delegate = new TestDelegate();
	    final WebSocketDelegate.OnIncomingFrameListener mockListener = mock(WebSocketDelegate.OnIncomingFrameListener.class);
		delegate.registerOnIncomingFrameListener(mockListener);
	    final WebSocketDelegate.Frame mockFrame = mock(WebSocketDelegate.Frame.class);
	    // Act:
	    delegate.notifyFrameReceived(mockFrame);
	    // Assert:
	    verify(mockListener).onFrameReceived(mockFrame);
	    verifyNoMoreInteractions(mockListener);
	}

    @Test public void testNotifyReceiveFrameWithoutRegisteredListeners() {
	    // Act:
    	// Only ensure that the delegate does cause any troubles.
	    new TestDelegate().notifyFrameReceived(mock(WebSocketDelegate.Frame.class));
	}

	private static class TestDelegate extends BaseWebSocketDelegate {

		@Override public void connect(@Nonnull SocketAddress remoteAddress, int timeout) throws IOException {}

		@Override public boolean isConnected() {
			return false;
		}

		@Override public void sendFrame(@Nonnull Frame frame) throws IOException {}

		@Override public void close() throws IOException {}

		@Override public boolean isClosed() {
			return false;
		}
	}
}