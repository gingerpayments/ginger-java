import com.gingerpayments.sdk.ApiClient.ApiClient;
import com.gingerpayments.sdk.Ginger;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Test;

public final class IdealPayment {
    @Test
    public void example() throws Exception {
        String endpoint = System.getenv("GINGER_ENDPOINT");
        String apiKey = System.getenv("GINGER_API_KEY");

        ApiClient client = Ginger.createClient(endpoint, apiKey);

        System.out.println(client.getIdealIssuers().toString());

        JSONObject order = client.createOrder(new JSONObject()
            .put("amount", 250)  // in cents
            .put("currency", "EUR")
            .put("transactions", new JSONArray()
                .put(new JSONObject()
                    .put("payment_method", "ideal")
                    .put("payment_method_details", new JSONObject()
                        .put("issuer_id", "BANKNL2Y")
                    )
                )
            )
        );

        System.out.println("Payment URL: " + (String) order.query("/transactions/0/payment_url"));
    }
}
