package com.gingerpayments.sdk;

import com.gingerpayments.sdk.ApiClient.ApiClient;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public final class GingerTest {
    @Test
    public void testItCreatesAClient() throws MalformedURLException {
        assertTrue(Ginger.createClient(new URL("https://www.example.com/"), "abc123") instanceof ApiClient);
    }

    @Test
    public void testItCreatesAClientWithDefaultHeaders() throws MalformedURLException {
        assertTrue(Ginger.createClient(new URL("https://www.example.com/"), "abc123", Map.of("foo", "bar")) instanceof ApiClient);
    }

    @Test
    public void testItCreatesAClientWithAStringUrl() throws MalformedURLException {
        assertTrue(Ginger.createClient("https://www.example.com/", "abc123") instanceof ApiClient);
    }
}
