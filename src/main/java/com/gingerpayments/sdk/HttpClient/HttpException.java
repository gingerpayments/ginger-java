package com.gingerpayments.sdk.HttpClient;

/**
 * Thrown when the HTTP client encounters an error.
 */
public final class HttpException extends Exception {
    /**
     * @param errorNumber HTTP library error number.
     * @param errorMessage HTTP library error message.
     * @param path Path used in the HTTP request.
     */
    public HttpException(int errorNumber, String errorMessage, String path) {
        super(
                String.format("HTTP error: %d: %s for %s",
                        errorNumber,
                        errorMessage,
                        path
                )
        );
    }
}
