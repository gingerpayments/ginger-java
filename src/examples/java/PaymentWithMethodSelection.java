import com.gingerpayments.sdk.ApiClient.ApiClient;
import com.gingerpayments.sdk.Ginger;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Test;

public class PaymentWithMethodSelection {
    @Test
    public void example() throws Exception {
        String endpoint = System.getenv("GINGER_ENDPOINT");
        String apiKey = System.getenv("GINGER_API_KEY");

        ApiClient client = Ginger.createClient(endpoint, apiKey);

        JSONObject order = client.createOrder(new JSONObject()
            .put("amount", 250)  // in cents
            .put("currency", "EUR")
        );

        System.out.println("Payment URL: " + order.getString("order_url"));
    }
}
