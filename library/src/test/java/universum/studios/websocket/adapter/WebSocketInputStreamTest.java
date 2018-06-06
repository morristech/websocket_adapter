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

import java.io.Closeable;
import java.io.IOException;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Martin Albedinsky
 */
public class WebSocketInputStreamTest {

	@Test public void testInstantiation() {
		// Arrange:
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		// Act:
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		// Assert:
		verify(mockDelegate).registerOnIncomingFrameListener(stream);
	}

	@Test public void testOnFrameReceived() throws IOException {
		// Arrange:
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		// Act:
		stream.onFrameReceived(new WebSocketFrame.Builder().payload(new byte[]{0, 1, 0, 1}).build());
		// Assert:
		assertThat(stream.available(), is(4));
	}

	@Test public void testOnFrameReceivedWithoutPayloadData() throws IOException {
		// Arrange:
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		// Act:
		stream.onFrameReceived(new WebSocketFrame.Builder().payload(new byte[0]).build());
		// Assert:
		assertThat(stream.available(), is(0));
	}

	@Test public void testOnFrameReceivedNotFinal() throws IOException {
		// Arrange:
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		// Act + Assert:
		stream.onFrameReceived(new WebSocketFrame.Builder().payload(new byte[]{0, 1, 0, 1}).isFinal(false).build());
		assertThat(stream.available(), is(0));
		stream.onFrameReceived(new WebSocketFrame.Builder().payload(new byte[]{0, 1, 0, 1}).isFinal(true).build());
		assertThat(stream.available(), is(8));
	}

	@Test public void testOnFrameReceivedWhenAlreadyClosed() throws IOException {
		// Arrange:
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		stream.close();
		// Act:
		stream.onFrameReceived(mock(WebSocketDelegate.Frame.class));
	}

	@Test public void testAvailable() throws IOException {
		// Arrange:
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		stream.onFrameReceived(new WebSocketFrame.Builder().payload(new byte[]{0, 1, 0, 1}).build());
		// Act + Assert:
		assertThat(stream.available(), is(4));
	}

	@Test public void testAvailableOnEmptyStream() throws IOException {
		// Arrange:
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		// Act + Assert:
		assertThat(stream.available(), is(0));
	}

	@Test(expected = IOException.class)
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void testAvailableWhenAlreadyClosed() throws IOException {
		// Arrange:
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		stream.close();
		// Act:
		stream.available();
	}

	@Test public void testSkip() throws IOException {
		// Arrange:
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		stream.onFrameReceived(new WebSocketFrame.Builder().payload(new byte[]{0, 1, 0, 1}).build());
		// Act + Assert:
		assertThat(stream.skip(4), is(4L));
		assertThat(stream.available(), is(0));
	}

	@Test public void testSkipOnEmptyStream() throws IOException {
		// Arrange:
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		// Act + Assert:
		assertThat(stream.skip(20), is(0L));
	}

	@Test(expected = IOException.class)
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void testSkipWhenAlreadyClosed() throws IOException {
		// Arrange:
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		stream.close();
		// Act:
		stream.skip(20);
	}

	@Test public void testMarkSupported() {
		// Arrange:
		final WebSocketInputStream stream = new WebSocketInputStream(mock(Closeable.class), mock(WebSocketDelegate.class));
		// Act + Assert:
		assertThat(stream.markSupported(), is(true));
	}

	@Test public void testMark() throws IOException {
		// Arrange:
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		stream.onFrameReceived(new WebSocketFrame.Builder().payload(new byte[]{0, 0, 1, 1}).build());
		// Act:
		stream.mark(0);
		// Assert:
		assertThat(stream.read(), is(0));
		assertThat(stream.read(), is(0));
		stream.reset();
		assertThat(stream.read(), is(0));
		assertThat(stream.read(), is(0));
		assertThat(stream.read(), is(1));
		assertThat(stream.read(), is(1));
	}

	@Test public void testResetOnEmptyStream() throws IOException {
		// Act:
		// Only ensure that marking does not cause any troubles when there is not stream active.
		new WebSocketInputStream(mock(Closeable.class),  mock(WebSocketDelegate.class)).reset();
	}

	@Test public void testMarkOnEmptyStream() {
		// Act:
		// Only ensure that marking does not cause any troubles when there is not stream active.
		new WebSocketInputStream(mock(Closeable.class),  mock(WebSocketDelegate.class)).mark(0);
	}

	@Test(expected = IOException.class)
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void testReadWhenAlreadyClosed() throws IOException {
		// Arrange:
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		stream.close();
		// Act:
		stream.read();
	}

	@Test public void testClose() throws IOException {
		// Arrange:
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		// Act:
		stream.close();
		stream.close();
		// Assert:
		verify(mockSocket).close();
		verifyNoMoreInteractions(mockSocket);
	}

	@Test(expected = IOException.class)
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void testDestroy() throws IOException {
		// Arrange:
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		// Act:
		stream.destroy();
		// Assert:
		verify(mockDelegate).unregisterOnIncomingFrameListener(stream);
		stream.read();
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Test public void testDestroyWhenReadingIsBlocked() throws Exception {
		// Arrange:
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketInputStream stream = new WebSocketInputStream(mockSocket, mockDelegate);
		final Thread thread = new Thread(() -> {
			try {
				stream.read();
			} catch (IOException e) {
				// Silently ignore.
			}
		});
		thread.start();
		Thread.sleep(100);
		// Act:
		stream.destroy();
	}
}