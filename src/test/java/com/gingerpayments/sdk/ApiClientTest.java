package com.gingerpayments.sdk;

import com.gingerpayments.sdk.ApiClient.ApiClient;
import com.gingerpayments.sdk.ApiClient.HttpRequestFailure;
import com.gingerpayments.sdk.ApiClient.JsonDecodeFailure;
import com.gingerpayments.sdk.ApiClient.ServerError;
import com.gingerpayments.sdk.HttpClient.HttpException;
import com.gingerpayments.sdk.HttpClient.MockHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public final class ApiClientTest {
    private MockHttpClient httpClient;
    private ApiClient apiClient;

    @Before
    public void setUp() {
        httpClient = new MockHttpClient();
        apiClient = new ApiClient(httpClient);
    }

    @Test
    public void testItGetsIdealIssuers() throws Exception {
        JSONArray expectedIssuers = new JSONArray()
            .put(new JSONObject()
                .put("id", "INGBNL2A")
                .put("list_type", "Deutschland")
                .put("name", "Issuer Simulation V3 - ING")
            )
            .put(new JSONObject()
                .put("id", "RABONL2U")
                .put("list_type", "Deutschland")
                .put("name", "Issuer Simulation V3 - RABO")
            );
        httpClient.setResponseToReturn(expectedIssuers.toString());

        JSONArray issuers = apiClient.getIdealIssuers();

        assertArrayEquals(
                new Object[] {
                    "GET",
                    "/ideal/issuers",
                    Map.of(),
                    null
                },
                httpClient.lastRequestData()
        );
        assertTrue(issuers.similar(expectedIssuers));
    }

    @Test
    public void testItGetsAnOrder() throws Exception {
        JSONObject expectedOrder = new JSONObject()
            .put("id", "fcbfdd3a-ea2c-4240-96b2-613d49b79a55")
            .put("transactions", new JSONArray()
                .put(new JSONObject()
                    .put("id", "ddc76c84-3fc2-4a16-85b9-a895f6bdc696")
                    .put("amount", 995)
                )
            );
        httpClient.setResponseToReturn(expectedOrder.toString());

        JSONObject order = apiClient.getOrder("fcbfdd3a-ea2c-4240-96b2-613d49b79a55");

        assertArrayEquals(
                new Object[] {
                    "GET",
                    "/orders/fcbfdd3a-ea2c-4240-96b2-613d49b79a55",
                    Map.of(),
                    null
                },
                httpClient.lastRequestData()
        );
        assertTrue(order.similar(expectedOrder));
    }

    @Test
    public void testItCreatesAnOrder() throws Exception {
        JSONObject expectedOrder = new JSONObject()
            .put("amount", 995)
            .put("currency", "EUR")
            .put("description", "My amazing order")
            .put("merchant_order_id", "my-custom-id-7131b462")
            .put("return_url", "https://www.example.com")
            .put("webhook_url", "https://www.example.com/hook")
            .put("customer", new JSONObject()
                .put("first_name", "John")
                .put("last_name", "Doe")
            )
            .put("extra", new JSONObject()
                .put("my-custom-data", "Foobar")
            )
            .put("transactions", new JSONArray()
                .put(new JSONObject()
                    .put("payment_method", "ideal")
                    .put("payment_method_details", new JSONObject()
                        .put("issuer_id", "INGBNL2A")
                    )
                    .put("expiration_period", "PT10M")
                )
            );
        httpClient.setResponseToReturn(expectedOrder.toString());

        JSONObject order = apiClient.createOrder(expectedOrder);

        assertArrayEquals(
                new Object[] {
                    "POST",
                    "/orders",
                    Map.of("Content-Type", "application/json"),
                    expectedOrder.toString()
                },
                httpClient.lastRequestData()
        );
        assertTrue(order.similar(expectedOrder));
    }

    @Test
    public void testItUpdatesAnOrder() throws Exception {
        JSONObject expectedOrder = new JSONObject()
            .put("id", "fcbfdd3a-ea2c-4240-96b2-613d49b79a55")
            .put("amount", 995)
            .put("currency", "EUR")
            .put("description", "My new description")
            .put("merchant_order_id", "my-custom-id-7131b462")
            .put("return_url", "https://www.example.com")
            .put("webhook_url", "https://www.example.com/hook")
            .put("customer", new JSONObject()
                .put("first_name", "John")
                .put("last_name", "Doe")
            )
            .put("extra", new JSONObject()
                .put("my-custom-data", "Foobar")
            )
            .put("transactions", new JSONArray()
                .put(new JSONObject()
                    .put("payment_method", "ideal")
                    .put("payment_method_details", new JSONObject()
                        .put("issuer_id", "INGBNL2A")
                    )
                    .put("expiration_period", "PT10M")
                )
            );
        httpClient.setResponseToReturn(expectedOrder.toString());

        JSONObject requestBody = new JSONObject()
            .put("description", "My new description");
        JSONObject order = apiClient.updateOrder("fcbfdd3a-ea2c-4240-96b2-613d49b79a55", requestBody);

        assertArrayEquals(
                new Object[] {
                    "PUT",
                    "/orders/fcbfdd3a-ea2c-4240-96b2-613d49b79a55",
                    Map.of("Content-Type", "application/json"),
                    requestBody.toString()
                },
                httpClient.lastRequestData()
        );
        assertTrue(order.similar(expectedOrder));
    }

    @Test
    public void testItRefundsAnOrder() throws Exception {
        JSONObject expectedOrder = new JSONObject()
            .put("id", "fcbfdd3a-ea2c-4240-96b2-613d49b79a55")
            .put("transactions", new JSONArray()
                .put(new JSONObject()
                    .put("id", "ddc76c84-3fc2-4a16-85b9-a895f6bdc696")
                    .put("amount", 995)
                )
            );
        httpClient.setResponseToReturn(expectedOrder.toString());

        JSONObject requestBody = new JSONObject()
            .put("amount", 123)
            .put("description", "My refund");
        JSONObject order = apiClient.refundOrder("fcbfdd3a-ea2c-4240-96b2-613d49b79a55", requestBody);

        assertArrayEquals(
                new Object[] {
                    "POST",
                    "/orders/fcbfdd3a-ea2c-4240-96b2-613d49b79a55/refunds",
                    Map.of("Content-Type", "application/json"),
                    requestBody.toString()
                },
                httpClient.lastRequestData()
        );
        assertTrue(order.similar(expectedOrder));
    }

    @Test
    public void testItCapturesAnOrderTransaction() throws Exception {
        apiClient.captureOrderTransaction(
            "fcbfdd3a-ea2c-4240-96b2-613d49b79a55",
            "ca3dfa6f-3dd3-4942-a358-b6852a407333"
        );

        assertArrayEquals(
                new Object[] {
                    "POST",
                    String.format(
                        "/orders/%s/transactions/%s/captures/",
                        "fcbfdd3a-ea2c-4240-96b2-613d49b79a55",
                        "ca3dfa6f-3dd3-4942-a358-b6852a407333"
                    ),
                    Map.of(),
                    null
                },
                httpClient.lastRequestData()
        );
    }

    @Test
    public void testItThrowsAnExceptionOnHttpClientError() {
        httpClient.setExceptionToThrow(new HttpException(1, "Whoops!", "/"));

        HttpRequestFailure thrown = assertThrows(
            HttpRequestFailure.class,
            () -> apiClient.getIdealIssuers()
        );
    }

    @Test
    public void testItThrowsAnExceptionOnJsonDecodeError() {
        httpClient.setResponseToReturn("definately not json");

        JsonDecodeFailure thrown = assertThrows(
            JsonDecodeFailure.class,
            () -> apiClient.getIdealIssuers()
        );
    }

    @Test
    public void testItThrowsAnExceptionOnServerError() {
        httpClient.setResponseToReturn(new JSONObject()
            .put("error", new JSONObject()
                .put("status", "503")
                .put("type", "ConnectionError")
                .put("value", "The server made a boo-boo")
            )
            .toString()
        );

        ServerError thrown = assertThrows(
            ServerError.class,
            () -> apiClient.getIdealIssuers()
        );
    }
}
