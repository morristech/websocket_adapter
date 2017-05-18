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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @author Martin Albedinsky
 */
public final class WebSocketFrameTest {
    
	@SuppressWarnings("unused")
	private static final String TAG = "WebSocketFrameTest";

    @Test
	public void testBuilderBuild() {
	    final byte[] payload = new byte[]{0, 1, 1, 1, 0};
	    final WebSocketFrame frame = new WebSocketFrame.Builder().payload(payload).build();
	    assertThat(frame, is(notNullValue()));
	    assertThat(frame.getPayload(), is(payload));
	    assertThat(frame.isFinal(), is(true));
	}

    @Test
	public void testBuilderBuildFinalFrame() {
	    final byte[] payload = new byte[]{0, 1, 1, 1, 0};
	    final WebSocketFrame frame = new WebSocketFrame.Builder().payload(payload).isFinal(false).build();
	    assertThat(frame, is(notNullValue()));
	    assertThat(frame.getPayload(), is(payload));
	    assertThat(frame.isFinal(), is(false));
	}

    @Test(expected = IllegalArgumentException.class)
	public void testBuilderBuildWithoutPayload() {
	    new WebSocketFrame.Builder().build();
	}
}
