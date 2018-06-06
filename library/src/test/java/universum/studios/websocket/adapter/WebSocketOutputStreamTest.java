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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Martin Albedinsky
 */
public class WebSocketOutputStreamTest {

	@Test
	public void testWriteBytes() throws Exception {
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketOutputStream stream = new WebSocketOutputStream(mockSocket, mockDelegate);
		stream.write(new byte[]{0}, 0, 1);
		verifyZeroInteractions(mockDelegate);
		stream.write(new byte[]{1, 1}, 0, 2);
		verifyZeroInteractions(mockDelegate);
		verifyZeroInteractions(mockSocket);
	}

	@Test(expected = IOException.class)
	public void testWriteBytesWhenAlreadyClosed() throws Exception {
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketOutputStream stream = new WebSocketOutputStream(mockSocket, mockDelegate);
		stream.close();
		stream.write(new byte[]{0}, 0, 1);
	}

	@Test
	public void testWriteByte() throws Exception {
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketOutputStream stream = new WebSocketOutputStream(mockSocket, mockDelegate);
		stream.write(0);
		verifyZeroInteractions(mockDelegate);
		stream.write(1);
		verifyZeroInteractions(mockDelegate);
		verifyZeroInteractions(mockSocket);
	}

	@Test(expected = IOException.class)
	public void testWriteByteWhenAlreadyClosed() throws Exception {
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketOutputStream stream = new WebSocketOutputStream(mockSocket, mockDelegate);
		stream.close();
		stream.write(0);
	}

	@Test
	public void testFlush() throws Exception {
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketOutputStream stream = new WebSocketOutputStream(mockSocket, mockDelegate);
		stream.write(0);
		verifyZeroInteractions(mockDelegate);
		stream.write(1);
		verifyZeroInteractions(mockDelegate);
		stream.write(new byte[]{1, 1}, 0, 2);
		verifyZeroInteractions(mockDelegate);
		stream.flush();
		verify(mockDelegate, times(1)).sendFrame(any(WebSocketDelegate.Frame.class));
		verifyNoMoreInteractions(mockDelegate);
		verifyZeroInteractions(mockSocket);
	}

	@Test
	public void testFlushWithoutBytesWritten() throws Exception {
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketOutputStream stream = new WebSocketOutputStream(mockSocket, mockDelegate);
		stream.flush();
		verifyZeroInteractions(mockDelegate);
		verifyZeroInteractions(mockSocket);
	}

	@Test(expected = IOException.class)
	public void testFlushWhenAlreadyClosed() throws Exception {
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketOutputStream stream = new WebSocketOutputStream(mockSocket, mockDelegate);
		stream.close();
		stream.flush();
	}

	@Test
	public void testClose() throws Exception {
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketOutputStream stream = new WebSocketOutputStream(mockSocket, mockDelegate);
		stream.write(0);
		stream.write(new byte[]{0}, 0, 1);
		stream.close();
		stream.close();
		verify(mockSocket, times(1)).close();
		verifyNoMoreInteractions(mockSocket);
		verifyZeroInteractions(mockDelegate);
	}

	@Test(expected = IOException.class)
	public void testDestroy() throws Exception {
		final Closeable mockSocket = mock(Closeable.class);
		final WebSocketDelegate mockDelegate = mock(WebSocketDelegate.class);
		final WebSocketOutputStream stream = new WebSocketOutputStream(mockSocket, mockDelegate);
		stream.destroy();
		stream.write(0);
	}
}
