package httpClientBuilder.connector;

public class ConnectorInitializer {

    private static volatile DefaultHttpConnector defaultHttpConnector;

    public static HttpConnector<String> createDefault() {
        if (defaultHttpConnector == null) {
            synchronized (DefaultHttpConnector.class) {
                if (defaultHttpConnector == null) {
                    defaultHttpConnector = new DefaultHttpConnector();
                }
            }
        }
        return defaultHttpConnector;
    }
}
