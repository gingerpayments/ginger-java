package com.gingerpayments.sdk.ApiClient;

/**
 * Thrown when an error occurs while processing the request.
 */
public final class HttpRequestFailure extends Exception {
    public HttpRequestFailure(Throwable cause) {
        super(cause);
    }
}
