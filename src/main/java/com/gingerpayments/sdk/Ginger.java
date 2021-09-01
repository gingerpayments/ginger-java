package com.gingerpayments.sdk;

import com.gingerpayments.sdk.ApiClient.ApiClient;
import com.gingerpayments.sdk.HttpClient.OkHttpClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Ginger API client builder.
 */
public final class Ginger {
    public static final String CLIENT_VERSION = "2.1.1";
    public static final String API_VERSION = "v1";

    /**
     * This class only contains static methods.
     */
    private Ginger() {}

    /**
     * Create and configure a new API client.
     *
     * @param endpoint Base API endpoint, without a trailing slash and version prefix.
     * @param apiKey Project API key.
     * @return Configured API client.
     * @throws MalformedURLException When an invalid endpoint is supplied.
     */
    public static ApiClient createClient(String endpoint, String apiKey) throws MalformedURLException {
        return createClient(endpoint, apiKey, Map.of());
    }

    /**
     * Create and configure a new API client.
     *
     * @param endpoint Base API endpoint, without a trailing slash and version prefix.
     * @param apiKey Project API key.
     * @return Configured API client.
     * @throws MalformedURLException When an invalid endpoint is supplied.
     */
    public static ApiClient createClient(URL endpoint, String apiKey) throws MalformedURLException {
        return createClient(endpoint, apiKey, Map.of());
    }

    /**
     * Create and configure a new API client with additional default headers.
     *
     * @param endpoint Base API endpoint, without a trailing slash and version prefix.
     * @param apiKey Project API key.
     * @param defaultHeaders HTTP headers that should be included in all requests.
     * @return Configured API client.
     * @throws MalformedURLException When an invalid endpoint is supplied.
     */
    public static ApiClient createClient(String endpoint, String apiKey, Map<String, String> defaultHeaders) throws MalformedURLException {
        return createClient(new URL(endpoint), apiKey, defaultHeaders);
    }

    /**
     * Create and configure a new API client with additional default headers.
     *
     * @param endpoint Base API endpoint, without a trailing slash and version prefix.
     * @param apiKey Project API key.
     * @param defaultHeaders HTTP headers that should be included in all requests.
     * @return Configured API client.
     * @throws MalformedURLException When an invalid endpoint is supplied.
     */
    public static ApiClient createClient(URL endpoint, String apiKey, Map<String, String> defaultHeaders) throws MalformedURLException {
        String userAgent = String.format(
            "Ginger-Java/%s (%s, %s %s)",
            CLIENT_VERSION,
            System.getProperty("os.name"),
            System.getProperty("java.runtime.name"),
            System.getProperty("java.runtime.version")
        );
        Map<String, String> headers = new HashMap<>(defaultHeaders);
        headers.put("User-Agent", userAgent);

        return new ApiClient(
            new OkHttpClient(new URL(endpoint, String.format("/%s", API_VERSION)), apiKey, headers)
        );
    }
}
