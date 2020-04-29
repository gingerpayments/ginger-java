package com.gingerpayments.sdk.HttpClient;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

public final class OkHttpClientTest {
    private HttpClient client;
    private MockWebServer server;

    @Before
    public void setUp() {
        server = new MockWebServer();
        client = new OkHttpClient(
            server.url("/v1"),
            "1a1b2e63c55e"
        );
    }

    @Test
    public void testItSendsARequest() throws Exception {
        server.enqueue(new MockResponse().setBody("not an empty body"));

        String response = client.request(
            "POST",
            "/foo/bar",
            Map.of("Content-Type", "text/plain"),
            "request data"
        );

        RecordedRequest request = server.takeRequest();
        assertEquals("/v1/foo/bar", request.getPath());
        assertEquals("POST", request.getMethod());
        assertEquals("request data", request.getBody().readUtf8());
        assertEquals("12", request.getHeader("Content-Length"));
        assertEquals("text/plain", request.getHeader("Content-Type"));
        assertEquals("Basic MWExYjJlNjNjNTVlOg==", request.getHeader("Authorization"));
    }

    @Test
    public void testItOmitsTheContentLengthHeaderWhenNoRequestData() throws Exception {
        server.enqueue(new MockResponse().setBody("not an empty body"));

        String response = client.request(
            "GET",
            "/foo/bar"
        );

        RecordedRequest request = server.takeRequest();
        assertEquals("GET", request.getMethod());
        assertNull(request.getHeader("Content-Length"));
    }

    @Test
    public void testItSetsDefaultHeaders() throws Exception {
        client = new OkHttpClient(
            server.url("/"),
            "1a1b2e63c55e",
            Map.of("X-Custom-Header", "foobar")
        );
        server.enqueue(new MockResponse().setBody("not an empty body"));

        String response = client.request(
            "GET",
            "/foo/bar"
        );

        RecordedRequest request = server.takeRequest();
        assertEquals("foobar", request.getHeader("X-Custom-Header"));
    }

    @Test
    public void testItReturnsNullOnEmptyResponseBody() throws Exception {
        server.enqueue(new MockResponse().setBody(""));

        String response = client.request(
            "GET",
            "/empty/response"
        );

        assertNull(response);
    }

    @Test
    public void testItHandlesJavaNetUrls() throws Exception {
        client = new OkHttpClient(
            server.url("/").url(),
            "1a1b2e63c55e"
        );
        server.enqueue(new MockResponse());

        client.request("GET", "/foo/bar");
    }

    @Test
    public void testItThrowsAnExceptionOnIoError() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        HttpException thrown = assertThrows(
            HttpException.class,
            () -> client.request("GET", "/error")
        );
    }
}
