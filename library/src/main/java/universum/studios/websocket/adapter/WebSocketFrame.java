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

import javax.annotation.Nonnull;

/**
 * Implementation of {@link WebSocketDelegate.Frame}.
 *
 * @author Martin Albedinsky
 * @since 1.0
 */
public final class WebSocketFrame implements WebSocketDelegate.Frame {

    /*
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "WebSocketFrame";

    /*
	 * Interface ===================================================================================
	 */

	/*
	 * Static members ==============================================================================
	 */

	/*
	 * Members =====================================================================================
	 */

	/**
	 * Payload data of this frame.
	 */
	private final byte[] payload;

	/**
	 * Boolean flag indicating whether this frame is a final frame.
	 */
	private final boolean isFinal;
	 
	/*
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of WebSocketFrame with data provided by the specified <var>builder</var>.
	 *
	 * @param builder The builder with data for the new frame.
	 */
	@SuppressWarnings("WeakerAccess")
	WebSocketFrame(final Builder builder) {
		this.payload = builder.payload;
		this.isFinal = builder.isFinal;
	}
	 
	/*
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Override @Nonnull public byte[] getPayload() {
		return payload;
	}

	/**
	 */
	@Override public boolean isFinal() {
		return isFinal;
	}

	/*
	 * Inner classes ===============================================================================
	 */

	/**
	 * Builder that may be used to create instances of {@link WebSocketFrame} with a desired data.
	 *
	 * <h3>Required parameters</h3>
	 * Parameters specified below are required in order to create a new instance of {@link WebSocketFrame}
	 * via {@link Builder#build()} successfully.
	 * <ul>
	 * <li>{@link #payload(byte[])}</li>
	 * </ul>
	 *
	 * @author Martin Albedinsky
	 * @since 1.0
	 */
	public static final class Builder {

		/**
		 * See {@link WebSocketFrame#payload}.
		 */
		byte[] payload;

		/**
		 * See {@link WebSocketFrame#isFinal}.
		 */
		boolean isFinal = true;

		/**
		 * Specifies a payload data for the new frame.
		 *
		 * @param payload The desired payload data.
		 * @return This builder to allow methods chaining.
		 *
		 * @see WebSocketFrame#getPayload()
		 */
		public Builder payload(@Nonnull final byte[] payload) {
			this.payload = payload;
			return this;
		}

		/**
		 * Specifies a boolean flag indicating whether the new frame should be a final frame.
		 * <p>
		 * Default value: {@code true}
		 *
		 * @param isFinal {@code True} if the new frame should be a final frame, {@code false}
		 *                otherwise.
		 * @return This builder to allow methods chaining.
		 *
		 * @see WebSocketFrame#isFinal()
		 */
		public Builder isFinal(final boolean isFinal) {
			this.isFinal = isFinal;
			return this;
		}

		/**
		 * Builds a new instance of WebSocketFrame with the data specified for this builder.
		 *
		 * @return WebSocketFrame instance ready to be delivered.
		 * @throws IllegalArgumentException If some of the required parameters is missing.
		 */
		@Nonnull public WebSocketFrame build() {
			if (payload == null) {
				throw new IllegalArgumentException("No payload specified.");
			}
			return new WebSocketFrame(this);
		}
	}
}