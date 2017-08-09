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

import javax.annotation.Nonnull;

/**
 * Implementation of {@link WebSocketDelegate.Frame}.
 *
 * @author Martin Albedinsky
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
	private final byte[] mPayload;

	/**
	 * Boolean flag indicating whether this frame is a final frame.
	 */
	private final boolean mFinal;
	 
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
		this.mPayload = builder.payload;
		this.mFinal = builder.isFinal;
	}
	 
	/*
	 * Methods =====================================================================================
	 */

	/**
	 */
	@Nonnull
	@Override
	public byte[] getPayload() {
		return mPayload;
	}

	/**
	 */
	@Override
	public boolean isFinal() {
		return mFinal;
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
	 */
	public static final class Builder {

		/**
		 * See {@link WebSocketFrame#mPayload}.
		 */
		byte[] payload;

		/**
		 * See {@link WebSocketFrame#mFinal}.
		 */
		boolean isFinal = true;

		/**
		 * Specifies a payload data for the new frame.
		 *
		 * @param payload The desired payload data.
		 * @return This builder to allow methods chaining.
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
		@Nonnull
		public WebSocketFrame build() {
			if (payload == null) {
				throw new IllegalArgumentException("No payload specified.");
			}
			return new WebSocketFrame(this);
		}
	}
}