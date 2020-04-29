package com.gingerpayments.sdk.HttpClient;

import java.util.Map;

public final class MockHttpClient implements HttpClient {
    private Object[] lastRequestData;
    private String responseToReturn;
    private HttpException exceptionToThrow;

    public String request(String method, String path) throws HttpException {
        return request(method, path, null, null);
    }

    public String request(String method, String path, Map<String, String> headers) throws HttpException {
        return request(method, path, headers, null);
    }

    public String request(String method, String path, Map<String, String> headers, String data) throws HttpException {
        lastRequestData = new Object[]{method, path, headers, data};

        if (exceptionToThrow != null) {
            throw exceptionToThrow;
        }

        return responseToReturn;
    }

    public Object[] lastRequestData() {
        return lastRequestData;
    }

    public void setResponseToReturn(String response) {
        responseToReturn = response;
    }

    public void setExceptionToThrow(HttpException exception) {
        exceptionToThrow = exception;
    }
}
