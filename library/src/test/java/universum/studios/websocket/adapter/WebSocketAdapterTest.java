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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketAddress;

import javax.annotation.Nonnull;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Martin Albedinsky
 */
public class WebSocketAdapterTest {

	@Test public void testOnConnectionListenerOnConnected() throws IOException {
		// Arrange:
		final TestDelegate delegate = new TestDelegate();
		final WebSocketAdapter adapter = new WebSocketAdapter(delegate);
		delegate.setClosed(false);
		delegate.setConnected(true);
		final InputStream inputStream = adapter.getInputStream();
		final OutputStream outputStream = adapter.getOutputStream();
		// Act:
		delegate.notifyConnected();
		// Assert:
		assertThat(adapter.getInputStream(), is(inputStream));
		assertThat(adapter.getOutputStream(), is(outputStream));
	}

	@Test public void testOnConnectionListenerOnDisconnected() throws IOException {
		// Arrange:
		final TestDelegate delegate = new TestDelegate();
		final WebSocketAdapter adapter = new WebSocketAdapter(delegate);
		delegate.setClosed(false);
		delegate.setConnected(true);
		final InputStream inputStream = adapter.getInputStream();
		final OutputStream outputStream = adapter.getOutputStream();
		// Act:
		delegate.notifyDisconnected();
		// Assert:
		assertThat(adapter.getInputStream(), is(not(inputStream)));
		assertThat(adapter.getOutputStream(), is(not(outputStream)));
	}

	@Test public void testConnect() throws IOException {
		// Arrange:
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		final SocketAddress mockAddress = mock(SocketAddress.class);
		// Act:
		adapter.connect(mockAddress);
		// Assert:
		verify(mockDelegate).connect(mockAddress, 0);
	}

	@Test public void testConnectWithTimeout() throws IOException {
		// Arrange:
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		final SocketAddress mockAddress = mock(SocketAddress.class);
		// Act:
		adapter.connect(mockAddress, 1000);
		// Assert:
		verify(mockDelegate).connect(mockAddress, 1000);
	}

	@Test public void testIsConnected() {
		// Arrange:
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		// Act + Assert:
		when(mockDelegate.isConnected()).thenReturn(false);
		assertThat(adapter.isConnected(), is(false));
		when(mockDelegate.isConnected()).thenReturn(true);
		assertThat(adapter.isConnected(), is(true));
		verify(mockDelegate, times(2)).isConnected();
	}

	@Test public void testGetInputStream() throws IOException {
		// Arrange:
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		when(mockDelegate.isConnected()).thenReturn(true);
		when(mockDelegate.isClosed()).thenReturn(false);
		// Act + Assert:
		assertThat(adapter.getInputStream(), is(notNullValue()));
		assertThat(adapter.getInputStream(), is(adapter.getInputStream()));
	}

	@Test public void testGetOutputStream() throws IOException {
		// Arrange:
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		when(mockDelegate.isConnected()).thenReturn(true);
		when(mockDelegate.isClosed()).thenReturn(false);
		// Act + Assert:
		assertThat(adapter.getOutputStream(), is(notNullValue()));
		assertThat(adapter.getOutputStream(), is(adapter.getOutputStream()));
	}

	@Test(expected = IOException.class)
	public void testGetInputStreamWhenNotOpened() throws IOException  {
		// Act:
		new WebSocketAdapter(mock(WebSocketDelegate.class)).getInputStream();
	}

	@Test(expected = IOException.class)
	public void testGetOutputStreamWhenNotOpened() throws IOException  {
		// Act:
		new WebSocketAdapter(mock(WebSocketDelegate.class)).getOutputStream();
	}

	@Test public void testClose() throws IOException {
		// Arrange:
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		when(mockDelegate.isClosed()).thenReturn(false);
		// Act + Assert:
		adapter.close();
		when(mockDelegate.isClosed()).thenReturn(true);
		adapter.close();
	}

	@Test public void testIsClosed() {
		// Arrange:
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		// Act + Assert:
		when(mockDelegate.isClosed()).thenReturn(false);
		assertThat(adapter.isClosed(), is(false));
		when(mockDelegate.isClosed()).thenReturn(true);
		assertThat(adapter.isClosed(), is(true));
		verify(mockDelegate, times(2)).isClosed();
	}

	@Test(expected = IOException.class)
	public void testAssertConnectedOrThrowExceptionWhenNotConnected() throws IOException {
		// Act:
		new WebSocketAdapter(mock(WebSocketDelegate.class)).assertConnectedOrThrowException();
	}

	@Test public void testAssertOpenedOrThrowExceptionWhenNotClosed() throws IOException {
		// Act:
		new WebSocketAdapter(mock(WebSocketDelegate.class)).assertOpenedOrThrowException();
	}

	@Test(expected = IOException.class)
	public void testAssertOpenedOrThrowExceptionWhenAlreadyClosed() throws IOException {
		// Arrange:
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		// Act + Assert:
		adapter.close();
		when(mockDelegate.isClosed()).thenReturn(true);
		adapter.assertOpenedOrThrowException();
	}

	@Test public void testDestroyStreams() throws IOException {
		// Arrange:
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		when(mockDelegate.isConnected()).thenReturn(true);
		when(mockDelegate.isClosed()).thenReturn(false);
		final InputStream inputStream = adapter.getInputStream();
		final OutputStream outputStream = adapter.getOutputStream();
		// Act:
		adapter.destroyStreams();
		// Assert:
		assertThat(adapter.getInputStream(), is(not(inputStream)));
		assertThat(adapter.getOutputStream(), is(not(outputStream)));
	}

	@Test public void testDestroyStreamsWhenNoStreamsWereCreated() {
		// Arrange + Act:
		new WebSocketAdapter(mock(WebSocketDelegate.class)).destroyStreams();
	}

	private static class TestDelegate extends BaseWebSocketDelegate {

		private boolean connected;
		private boolean closed;

		@Override public void connect(@Nonnull SocketAddress remoteAddress, int timeout) throws IOException {}

		void setConnected(boolean connected) {
			this.connected = connected;
		}

		@Override public boolean isConnected() {
			return connected;
		}

		@Override public void sendFrame(@Nonnull Frame frame) throws IOException {}

		@Override public void close() throws IOException {}

		void setClosed(boolean closed) {
			this.closed = closed;
		}

		@Override public boolean isClosed() {
			return closed;
		}
	}
}