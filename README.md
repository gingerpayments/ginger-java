# Ginger Java bindings

[![Build Status](https://img.shields.io/travis/gingerpayments/ginger-java)](https://travis-ci.org/gingerpayments/ginger-java)
[![Maven Central](https://img.shields.io/maven-central/v/com.gingerpayments/ginger-java)](https://mvnrepository.com/artifact/com.gingerpayments/ginger-java)
[![MIT License](https://img.shields.io/badge/license-MIT-brightgreen.svg)](https://github.com/gingerpayments/ginger-java/blob/master/LICENSE)

## Requirements

* Java 8 or later

## Installation

Gradle (`build.gradle`):
```
implementation("com.gingerpayments:ginger-java:2.1.0")
```

Maven (`pom.xml`):
```
<dependency>
    <groupId>com.gingerpayments</groupId>
    <artifactId>ginger-java</artifactId>
    <version>2.1.0</version>
</dependency>
```

The library jar file can also be downloaded directly from [Maven Central](https://search.maven.org/search?q=ginger-java).

## Getting started

First create a new API client with your API key and API endpoint:

```java
import com.gingerpayments.sdk.ApiClient.ApiClient;
import com.gingerpayments.sdk.Ginger;

import org.json.JSONArray;
import org.json.JSONObject;

ApiClient client = Ginger.createClient("https://api.example.com", "your-api-key");
```

### Initiating a payment

You can start a new payment by creating a new order:

```java
JSONObject order = client.createOrder(new JSONObject()
    .put("merchant_order_id", "my-custom-order-id-12345")
    .put("currency", "EUR")
    .put("amount", 2500)  // in cents
    .put("description", "Purchase order 12345")
    .put("return_url", "https://www.example.com")
    .put("transactions", new JSONArray()
        .put(new JSONObject()
            .put("payment_method", "credit-card")
        )
    )
);
```

Once you've created your order, a transaction is created and associated with it. You will need to redirect the user to
the transaction's payment URL, which you can retrieve as follows:

```java
String paymentUrl = order.getString("order_url");
```

It is also recommended that you store the order's ID somewhere, so you can retrieve information about it later:

```java
String orderId = order.getString("id");
```

There is a lot more data related to an order. Please refer to the API documentation provided by your PSP to learn more
about the various payment methods and options.

### Getting an order

If you want to retrieve an existing order, use the `getOrder` method on the client:

```java
JSONObject order = client.getOrder(orderId);
```

This will return an associative array with all order information.

### Updating an order

Some fields are not read-only and you are able to update them after order has been created. You can do this using
the `updateOrder` method on the client:

```java
JSONObject order = client.getOrder(orderId);
order.put("description", "New Order Description");
JSONObject updatedOrder = client.updateOrder(orderId, order);
```

### Initiating a refund

You can refund an existing order by using the `refundOrder` method on the client:

```java
JSONObject refundOrder = client.refundOrder(orderId, new JSONObject().put("amount", 123).put("description", "My refund"));
```

### Capturing a transaction of an order

You can initiate a capture of an order's transaction by using the `captureOrderTransaction` method:

```
client.captureOrderTransaction(orderId, transactionId);
```

### Getting the iDEAL issuers

When you create an order with the iDEAL payment method, you need to provide an issuer ID. The issuer ID is an identifier
of the bank the user has selected. You can retrieve all possible issuers by using the `getIdealIssuers` method:

```java
JSONArray issuers = client.getIdealIssuers();
```

You can then use this information to present a list to the user of possible banks to choose from.


### Custom requests

You can send any request that the API accepts using the `send` method. E.g. instead of using the `createOrder` method
you could also use the following:

```java
String result = client.send(
    "POST", // Request method
    "/orders", // API path
    orderData // Data to send with the request; optional
);
```

The `result` variable would then contain the JSON string returned by the API.

## Running the examples

```
export GINGER_ENDPOINT="https://api.example.com"
export GINGER_API_KEY="your-api-key"

./gradlew examples --tests IdealPayment
./gradlew examples --tests PaymentWithMethodSelection
```

## Custom HTTP client

This library ships with its own minimal HTTP client for compatibility reasons. If you would like to use a different HTTP
client, you can do so by implementing the `com.gingerpayments.sdk.HttpClient.HttpClient` interface and then constructing
your own client:

```java
HttpClient myHttpClient = new MyHttpClient();
ApiClient client = new ApiClient(myHttpClient);
```

Make sure your HTTP client prefixes the endpoint URL and API version to all requests, and uses HTTP basic auth to
authenticate with the API using your API key.

## API documentation

For the complete API documentation please prefer to the resources provided by your PSP.
