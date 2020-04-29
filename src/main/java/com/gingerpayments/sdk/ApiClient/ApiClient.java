package com.gingerpayments.sdk.ApiClient;

import com.gingerpayments.sdk.HttpClient.HttpClient;
import com.gingerpayments.sdk.HttpClient.HttpException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic client for Ginger platform APIs.
 */
public final class ApiClient {
    private final HttpClient httpClient;

    public ApiClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Get a list of possible iDEAL issuers.
     *
     * @return The iDEAL issuers.
     * @throws HttpRequestFailure When an error occurred while processing the request.
     * @throws JsonDecodeFailure When the response contained invalid JSON.
     * @throws ServerError When the server returned an error message.
     */
    public JSONArray getIdealIssuers() throws HttpRequestFailure, JsonDecodeFailure, ServerError {
        return parseArray(send("GET", "/ideal/issuers"));
    }

    /**
     * Get an order.
     *
     * @param id The order ID.
     * @return The order.
     * @throws HttpRequestFailure When an error occurred while processing the request.
     * @throws JsonDecodeFailure When the response contained invalid JSON.
     * @throws ServerError When the server returned an error message.
     */
    public JSONObject getOrder(String id) throws HttpRequestFailure, JsonDecodeFailure, ServerError {
        return parseObject(send("GET", String.format("/orders/%s", id)));
    }

    /**
     * Create a new order.
     *
     * @param orderData Order attributes and values to create.
     * @return The newly created order.
     * @throws HttpRequestFailure When an error occurred while processing the request.
     * @throws JsonDecodeFailure When the response contained invalid JSON.
     * @throws ServerError When the server returned an error message.
     */
    public JSONObject createOrder(JSONObject orderData) throws HttpRequestFailure, JsonDecodeFailure, ServerError {
        return parseObject(send("POST", "/orders", orderData.toString()));
    }

    /**
     * Update an order.
     *
     * @param id        The ID of the order to update.
     * @param orderData Order attributes and values to update.
     * @return The newly updated order.
     * @throws HttpRequestFailure When an error occurred while processing the request.
     * @throws JsonDecodeFailure When the response contained invalid JSON.
     * @throws ServerError When the server returned an error message.
     */
    public JSONObject updateOrder(String id, JSONObject orderData) throws HttpRequestFailure, JsonDecodeFailure, ServerError {
        return parseObject(send("PUT", String.format("/orders/%s", id), orderData.toString()));
    }

    /**
     * Refund an order.
     *
     * @param id        The ID of the order to update.
     * @param orderData Refund data.
     * @return The newly updated order.
     * @throws HttpRequestFailure When an error occurred while processing the request.
     * @throws JsonDecodeFailure When the response contained invalid JSON.
     * @throws ServerError When the server returned an error message.
     */
    public JSONObject refundOrder(String id, JSONObject orderData) throws HttpRequestFailure, JsonDecodeFailure, ServerError {
        return parseObject(send("POST", String.format("/orders/%s/refunds", id), orderData.toString()));
    }

    /**
     * Capture an order transaction.
     *
     * @param orderId       The ID of the order.
     * @param transactionId The ID of the transaction to capture.
     * @throws HttpRequestFailure When an error occurred while processing the request.
     * @throws JsonDecodeFailure When the response contained invalid JSON.
     * @throws ServerError When the server returned an error message.
     */
    public void captureOrderTransaction(String orderId, String transactionId) throws HttpRequestFailure, JsonDecodeFailure, ServerError {
        send("POST", String.format("/orders/%s/transactions/%s/captures/", orderId, transactionId));
    }

    /**
     * Send a request to the API.
     *
     * @param method HTTP request method
     * @param path   URL path to call
     * @return Server response.
     * @throws HttpRequestFailure When an error occurred while processing the request.
     * @throws JsonDecodeFailure When the response contained invalid JSON.
     * @throws ServerError When the server returned an error message.
     */
    public String send(String method, String path) throws HttpRequestFailure, JsonDecodeFailure, ServerError {
        return send(method, path, null);
    }

    /**
     * Send a request to the API.
     *
     * @param method HTTP request method
     * @param path   URL path to call
     * @param data   Request data to send
     * @return Server response.
     * @throws HttpRequestFailure When an error occurred while processing the request.
     * @throws JsonDecodeFailure When the response contained invalid JSON.
     * @throws ServerError When the server returned an error message.
     */
    public String send(String method, String path, String data) throws HttpRequestFailure, JsonDecodeFailure, ServerError {
        String response;

        try {
            Map<String, String> headers = new HashMap<>();

            if (data != null) {
                headers.put("Content-Type", "application/json");
            }

            response = httpClient.request(method, path, headers, data);
        } catch (HttpException exception) {
            throw new HttpRequestFailure(exception);
        }

        return interpretResponse(response);
    }

    /**
     * Check response body for error messages.
     *
     * @param response Response data received from the server.
     * @return Reponse data.
     * @throws ServerError When the server returned an error message.
     */
    private String interpretResponse(String response) throws ServerError {
        if (response == null) {
            return null;
        }

        try {
            JSONObject parsedResponse = parseObject(response);

            if (parsedResponse.has("error")) {
                throw new ServerError(parsedResponse.getJSONObject("error"));
            }
        } catch (JsonDecodeFailure ignored) {
            // ignore exception, we only parse error objects here
        }

        return response;
    }

    /**
     * @param input String to be parsed.
     * @return Parsed JSON object.
     * @throws JsonDecodeFailure
     */
    private JSONObject parseObject(String input) throws JsonDecodeFailure {
        try {
            return new JSONObject(input);
        } catch (JSONException exception) {
            throw new JsonDecodeFailure(exception);
        }
    }

    /**
     * @param input String to be parsed.
     * @return Parsed JSON array.
     * @throws JsonDecodeFailure
     */
    private JSONArray parseArray(String input) throws JsonDecodeFailure {
        try {
            return new JSONArray(input);
        } catch (JSONException exception) {
            throw new JsonDecodeFailure(exception);
        }
    }
}
