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

import java.io.Closeable;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Martin Albedinsky
 */
public class WebSocketInputStreamTest {

	@SuppressWarnings("unused")
	private static final String TAG = "WebSocketInputStreamTest";

	@Test
	public void test() {
		// todo:
		assertThat(true, is(true));
	}

	@Test(expected = IOException.class)
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void testReadWhenAlreadyClosed() throws Exception {
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		stream.close();
		stream.read();
	}

	@Test
	public void testClose() throws Exception {
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		stream.close();
		stream.close();
		verify(mockSocket, times(1)).close();
		verifyNoMoreInteractions(mockSocket);
	}
}
