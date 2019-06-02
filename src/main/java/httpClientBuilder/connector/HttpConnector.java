package httpClientBuilder.connector;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public abstract class HttpConnector<T> {
    private CloseableHttpClient httpClient = getHttpClient();
    private HttpContext httpContext = getHttpContext();
    private HttpHost httpHost = getHttpHost();

    public T execute(HttpUriRequest request) {

        try {
            return httpClient.execute(
                    httpHost == null ? URIUtils.extractHost(request.getURI()) : httpHost,
                    request,
                    (response) -> handle(request, response),
                    httpContext);
        } catch (IOException e) {
            return handleException(request, e);
        }
    }

    public HttpContext getHttpContext() {
        return null;
    }

    public HttpHost getHttpHost() {
        return null;
    }

    public CloseableHttpClient getHttpClient() {
        return InstanceHttpClient.getInstance();
    }

    public T handle(HttpRequest request, HttpResponse response) {
        try {
            return handleResponse(request, response);
        } catch (IOException e) {
            return handleException(request, e);
        }
    }

    public T handleException(HttpRequest request, IOException e) {
        throw new RuntimeException(e);
    }

    public abstract T handleResponse(HttpRequest request, HttpResponse response) throws IOException;
}
