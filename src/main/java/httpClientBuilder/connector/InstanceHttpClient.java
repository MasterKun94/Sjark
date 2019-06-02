package httpClientBuilder.connector;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class InstanceHttpClient {
    private static volatile CloseableHttpClient httpClient;

    public static CloseableHttpClient getInstance() {
        if (httpClient == null) {
            synchronized (InstanceHttpClient.class) {
                if (httpClient == null) {
                    httpClient = HttpClients.createDefault();
                }
            }
        }
        return httpClient;
    }
}