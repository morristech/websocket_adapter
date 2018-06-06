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


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @author Martin Albedinsky
 */
public final class WebSocketFrameTest {
    
    @Test public void testInstantiation() {
	    // Arrange:
	    final byte[] payload = new byte[]{0, 1, 1, 1, 0};
	    // Act:
	    final WebSocketFrame frame = new WebSocketFrame.Builder().payload(payload).build();
	    // Assert:
	    assertThat(frame, is(notNullValue()));
	    assertThat(frame.getPayload(), is(payload));
	    assertThat(frame.isFinal(), is(true));
	}

    @Test public void testInstantiationOfFinalFrame() {
	    // Arrange:
    	final byte[] payload = new byte[]{0, 1, 1, 1, 0};
	    // Act:
    	final WebSocketFrame frame = new WebSocketFrame.Builder().payload(payload).isFinal(false).build();
	    // Assert:
	    assertThat(frame, is(notNullValue()));
	    assertThat(frame.getPayload(), is(payload));
	    assertThat(frame.isFinal(), is(false));
	}

    @Test(expected = IllegalArgumentException.class)
	public void testInstantiationWithoutPayload() {
	    // Act:
    	new WebSocketFrame.Builder().build();
	}
}