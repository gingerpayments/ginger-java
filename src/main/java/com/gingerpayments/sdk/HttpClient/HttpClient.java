package com.gingerpayments.sdk.HttpClient;

import java.util.Map;

/**
 * Custom HTTP interface because we do not want to depend on too many external
 * packages.
 */
public interface HttpClient {
    /**
     * Submit an HTTP request.
     *
     * @param method HTTP method
     * @param path Request path
     * @return Response body
     * @throws HttpException When an error occurred while executing the request.
     */
    String request(String method, String path) throws HttpException;

    /**
     * Submit an HTTP request with additional headers.
     *
     * @param method HTTP method
     * @param path Request path
     * @param headers Request headers
     * @return Response body
     * @throws HttpException When an error occurred while executing the request.
     */
    String request(String method, String path, Map<String, String> headers) throws HttpException;

    /**
     * Submit an HTTP request with additional headers and a request body.
     *
     * This must be used for request methods that require a body, i.e. POST,
     * PATCH and PUT.
     *
     * @param method HTTP method
     * @param path Request path
     * @param headers Request headers
     * @param data Request body
     * @return Response body
     * @throws HttpException When an error occurred while executing the request.
     */
    String request(String method, String path, Map<String, String> headers, String data) throws HttpException;
}
