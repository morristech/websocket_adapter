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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Martin Albedinsky
 */
public class WebSocketAdapterTest {

	@SuppressWarnings("unused")
	private static final String TAG = "WebSocketAdapterTest";

	@Test
	public void testClose() throws Exception {
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		adapter.close();
	}

	@Test(expected = IOException.class)
	public void testAssertConnectedOrThrowExceptionWhenNotConnected() throws Exception {
		new WebSocketAdapter(mock(WebSocketDelegate.class)).assertConnectedOrThrowException();
	}

	@Test
	public void testAssertOpenedOrThrowExceptionWhenNotClosed() throws Exception {
		new WebSocketAdapter(mock(WebSocketDelegate.class)).assertOpenedOrThrowException();
	}

	@Test(expected = IOException.class)
	public void testAssertOpenedOrThrowExceptionWhenAlreadyClosed() throws Exception {
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketAdapter adapter = new WebSocketAdapter(mockDelegate);
		adapter.close();
		when(mockDelegate.isClosed()).thenReturn(true);
		adapter.assertOpenedOrThrowException();
	}

	@Test(expected = IOException.class)
	public void testGetInputStreamWhenNotOpened() throws Exception  {
		new WebSocketAdapter(mock(WebSocketDelegate.class)).getInputStream();
	}

	@Test(expected = IOException.class)
	public void testGetOutputStreamWhenNotOpened() throws Exception  {
		new WebSocketAdapter(mock(WebSocketDelegate.class)).getOutputStream();
	}
}
