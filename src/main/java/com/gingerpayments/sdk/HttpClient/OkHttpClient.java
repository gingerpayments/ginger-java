package com.gingerpayments.sdk.HttpClient;

import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * HTTP client implementation using OkHttp.
 *
 * When an HTTP error is encountered, it uses the HTTP status (4xx or 5xx) as
 * the <code>errorNumber</code>; for other errors, <code>-1</code> is used.
 */
public final class OkHttpClient implements HttpClient {
    private final HttpUrl endpoint;
    private final String apiKey;
    private final Map<String, String> defaultHeaders;
    private final okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient();

    private static final MediaType MEDIA_TYPE_UNKNOWN = MediaType.parse("");

    public OkHttpClient(URL endpoint, String apiKey) {
        this(endpoint, apiKey, Map.of());
    }

    public OkHttpClient(URL endpoint, String apiKey, Map<String, String> defaultHeaders) {
        this(HttpUrl.parse(endpoint.toString()), apiKey, defaultHeaders);
    }

    public OkHttpClient(HttpUrl endpoint, String apiKey) {
        this(endpoint, apiKey, Map.of());
    }

    public OkHttpClient(HttpUrl endpoint, String apiKey, Map<String, String> defaultHeaders) {
        this.endpoint = endpoint;
        this.apiKey = apiKey;
        this.defaultHeaders = defaultHeaders;
    }

    public String request(String method, String path) throws HttpException {
        return request(method, path, null, null);
    }

    public String request(String method, String path, Map<String, String> headers) throws HttpException {
        return request(method, path, headers, null);
    }

    public String request(String method, String path, Map<String, String> headers, String data) throws HttpException {
        Request.Builder builder = new Request.Builder();
        RequestBody requestBody = null;

        if (defaultHeaders != null) {
            builder.headers(Headers.of(defaultHeaders));
        }

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.header(header.getKey(), header.getValue());
            }
        }

        if (data != null) {
            requestBody = RequestBody.create(data, MEDIA_TYPE_UNKNOWN);
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        HttpUrl requestUrl = endpoint.newBuilder()
            .addPathSegments(path)
            .build();
        Request request = builder
            .url(requestUrl)
            .method(method, requestBody)
            .header("Authorization", Credentials.basic(apiKey, ""))
            .build();

        try {
            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new HttpException(response.code(), response.message(), path);
            }

            ResponseBody body = response.body();

            if (body == null) {
                return null;
            }

            if (body.contentLength() == 0) {
                return null;
            }

            return body.string();
        } catch (IOException exception) {
            throw new HttpException(-1, exception.toString(), path);
        }
    }
}
