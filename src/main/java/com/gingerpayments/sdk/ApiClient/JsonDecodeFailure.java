package com.gingerpayments.sdk.ApiClient;

/**
 * Thrown when the API response does not contain valid JSON.
 */
public final class JsonDecodeFailure extends Exception {
    public JsonDecodeFailure(Throwable cause) {
        super(cause);
    }
}
