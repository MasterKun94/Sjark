package httpClientBuilder;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.Objects;

public class HttpConnector<T> {
    private CloseableHttpClient httpClient;
    private HttpContext httpContext;
    private HttpHost httpHost;
    private ResponseHandler<T> responseHandler;


    public static <T> T execute(HttpConnector<T> connector, HttpUriRequest request) {
        if (connector.httpClient == null) connector.httpClient = InstanceHttpClient.getDefault();
        if (connector.httpHost == null) connector.httpHost = URIUtils.extractHost(request.getURI());
        org.apache.http.client.ResponseHandler<T> responseHandler = response -> {
            try {
                return connector.responseHandler.handleResponse(request, response);
            } catch (IOException e) {
                return connector.responseHandler.handleException(request, e);
            }
        };
        try {
            return connector.httpClient.execute(
                    connector.httpHost,
                    request,
                    responseHandler,
                    connector.httpContext);
        } catch (IOException e) {
            return connector.responseHandler.handleException(request, e);
        }
    }

    private HttpConnector() {}

    public static <T> HttpConnector<T> of(ResponseHandler<T> responseHandler) {
        Objects.requireNonNull(responseHandler);
        HttpConnector<T> connector = new HttpConnector<>();
        connector.responseHandler = responseHandler;
        return connector;
    }

    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    public void setHttpHost(HttpHost httpHost) {
        this.httpHost = httpHost;
    }
}
