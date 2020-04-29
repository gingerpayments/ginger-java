package com.gingerpayments.sdk.ApiClient;

import org.json.JSONObject;

/**
 * Thrown when the API returns an error message.
 */
public final class ServerError extends Exception {
    public ServerError(JSONObject error) {
        super(formatMessage(error));
    }

    private static String formatMessage(JSONObject error) {
        return String.format(
                "%s(%s): %s",
                error.getString("type"),
                error.getString("status"),
                error.getString("value")
        );
    }
}
