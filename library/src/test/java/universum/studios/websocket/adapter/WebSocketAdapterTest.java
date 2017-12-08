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

	@Test
	public void testOnConnectionListenerOnConnected() throws IOException {
		final TestDelegate delegate = new TestDelegate();
		final WebSocketAdapter adapter = new WebSocketAdapter(delegate);
		delegate.setClosed(false);
		delegate.setConnected(true);
		final InputStream inputStream = adapter.getInputStream();
		final OutputStream outputStream = adapter.getOutputStream();
		delegate.notifyConnected();
		assertThat(adapter.getInputStream(), is(inputStream));
		assertThat(adapter.getOutputStream(), is(outputStream));
	}

	@Test
	public void testOnConnectionListenerOnDisconnected() throws IOException {
		final TestDelegate delegate = new TestDelegate();
		final WebSocketAdapter adapter = new WebSocketAdapter(delegate);
		delegate.setClosed(false);
		delegate.setConnected(true);
		final InputStream inputStream = adapter.getInputStream();
		final OutputStream outputStream = adapter.getOutputStream();
		delegate.notifyDisconnected();
		assertThat(adapter.getInputStream(), is(not(inputStream)));
		assertThat(adapter.getOutputStream(), is(not(outputStream)));
	}

	@Test
	public void testConnect() throws IOException {
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		final SocketAddress mockAddress = mock(SocketAddress.class);
		adapter.connect(mockAddress);
		verify(mockDelegate, times(1)).connect(mockAddress, 0);
	}

	@Test
	public void testConnectWithTimeout() throws IOException {
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		final SocketAddress mockAddress = mock(SocketAddress.class);
		adapter.connect(mockAddress, 1000);
		verify(mockDelegate, times(1)).connect(mockAddress, 1000);
	}

	@Test
	public void testIsConnected() {
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		when(mockDelegate.isConnected()).thenReturn(false);
		assertThat(adapter.isConnected(), is(false));
		when(mockDelegate.isConnected()).thenReturn(true);
		assertThat(adapter.isConnected(), is(true));
		verify(mockDelegate, times(2)).isConnected();
	}

	@Test
	public void testGetInputStream() throws IOException {
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		when(mockDelegate.isConnected()).thenReturn(true);
		when(mockDelegate.isClosed()).thenReturn(false);
		assertThat(adapter.getInputStream(), is(notNullValue()));
		assertThat(adapter.getInputStream(), is(adapter.getInputStream()));
	}

	@Test
	public void testGetOutputStream() throws IOException {
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		when(mockDelegate.isConnected()).thenReturn(true);
		when(mockDelegate.isClosed()).thenReturn(false);
		assertThat(adapter.getOutputStream(), is(notNullValue()));
		assertThat(adapter.getOutputStream(), is(adapter.getOutputStream()));
	}

	@Test(expected = IOException.class)
	public void testGetInputStreamWhenNotOpened() throws IOException  {
		new WebSocketAdapter(mock(WebSocketDelegate.class)).getInputStream();
	}

	@Test(expected = IOException.class)
	public void testGetOutputStreamWhenNotOpened() throws IOException  {
		new WebSocketAdapter(mock(WebSocketDelegate.class)).getOutputStream();
	}

	@Test
	public void testClose() throws IOException {
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		when(mockDelegate.isClosed()).thenReturn(false);
		adapter.close();
		when(mockDelegate.isClosed()).thenReturn(true);
		adapter.close();
	}

	@Test
	public void testIsClosed() {
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		when(mockDelegate.isClosed()).thenReturn(false);
		assertThat(adapter.isClosed(), is(false));
		when(mockDelegate.isClosed()).thenReturn(true);
		assertThat(adapter.isClosed(), is(true));
		verify(mockDelegate, times(2)).isClosed();
	}

	@Test(expected = IOException.class)
	public void testAssertConnectedOrThrowExceptionWhenNotConnected() throws IOException {
		new WebSocketAdapter(mock(WebSocketDelegate.class)).assertConnectedOrThrowException();
	}

	@Test
	public void testAssertOpenedOrThrowExceptionWhenNotClosed() throws IOException {
		new WebSocketAdapter(mock(WebSocketDelegate.class)).assertOpenedOrThrowException();
	}

	@Test(expected = IOException.class)
	public void testAssertOpenedOrThrowExceptionWhenAlreadyClosed() throws IOException {
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		adapter.close();
		when(mockDelegate.isClosed()).thenReturn(true);
		adapter.assertOpenedOrThrowException();
	}

	@Test
	public void testDestroyStreams() throws IOException {
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		when(mockDelegate.isConnected()).thenReturn(true);
		when(mockDelegate.isClosed()).thenReturn(false);
		final InputStream inputStream = adapter.getInputStream();
		final OutputStream outputStream = adapter.getOutputStream();
		adapter.destroyStreams();
		assertThat(adapter.getInputStream(), is(not(inputStream)));
		assertThat(adapter.getOutputStream(), is(not(outputStream)));
	}

	@Test
	public void testDestroyStreamsWhenNoStreamsWereCreated() {
		new WebSocketAdapter(mock(WebSocketDelegate.class)).destroyStreams();
	}

	private static class TestDelegate extends BaseWebSocketDelegate {

		private boolean connected;
		private boolean closed;

		@Override
		public void connect(@Nonnull SocketAddress remoteAddress, int timeout) throws IOException {
		}

		void setConnected(boolean connected) {
			this.connected = connected;
		}

		@Override
		public boolean isConnected() {
			return connected;
		}

		@Override
		public void sendFrame(@Nonnull Frame frame) throws IOException {
		}

		@Override
		public void close() throws IOException {
		}

		void setClosed(boolean closed) {
			this.closed = closed;
		}

		@Override
		public boolean isClosed() {
			return closed;
		}
	}
}
