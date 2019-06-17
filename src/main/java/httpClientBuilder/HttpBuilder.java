package httpClientBuilder;

import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class HttpBuilder {
    private static final String EQ = "=";
    private static final String AND = "&";

    private HttpRequestBase request;
    private String URL;
    private HttpHost httpHost;
    private HttpContext httpContext;
    private CloseableHttpClient httpClient;

    private static HttpBuilder start(HttpRequestBase request, String url) {

        HttpBuilder builder = new HttpBuilder();
        builder.URL = url;
        builder.request = request;
        return builder;
    }

    public static HttpBuilder post(String url) {
        return start(new HttpPost(), url);
    }

    public static HttpBuilder get(String url) {
        return start(new HttpGet(), url);
    }

    public static HttpBuilder put(String url) {
        return start(new HttpPut(), url);
    }

    public static HttpBuilder delete(String url) {
        return start(new HttpDelete(), url);
    }

    public HttpBuilder params(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return this;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?");
        Set<String> keys = params.keySet();
        for (String key : keys) {
            builder.append(key).append(EQ).append(params.get(key)).append(AND);

        }
        builder.deleteCharAt(builder.length() - 1);
        URL = URL + builder.toString();
        System.out.println(URL);
        return this;
    }

    public HttpBuilder headers(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return this;
        }
        Set<String> keys = headers.keySet();
        keys.forEach(key -> request.setHeader(key, headers.get(key)));
        return this;
    }

    public HttpBuilder rest() {
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");
        return this;
    }

    public HttpBuilder header(String head, String value) {
        request.setHeader(head, value);
        return this;
    }

    public HttpBuilder entity(String entity) {
        if (request instanceof HttpPost) {
            ((HttpPost) request).setEntity(new StringEntity(entity, Charsets.UTF_8));
        } else if (request instanceof HttpPut) {
            ((HttpPut) request).setEntity(new StringEntity(entity, Charsets.UTF_8));
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    public HttpBuilder charset(Charset charset) {
        request.setHeader("Charset", charset.toString());
        return this;
    }

    public HttpBuilder setHttpHost(HttpHost httpHost) {
        this.httpHost = httpHost;
        return this;
    }

    public HttpBuilder setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
        return this;
    }

    public HttpBuilder setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public <T> HttpResponseBuilder<T> execute(HttpConnector<T> connector) {
        request.setURI(URI.create(URL));

        return new HttpResponseBuilder<>(() -> HttpConnector.execute(connector, request));
    }

    public class HttpResponseBuilder<T> {
        private Supplier<T> supplier;

        HttpResponseBuilder(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        public CompletableFuture<T> async(Executor executor) {
            return CompletableFuture.supplyAsync(supplier, executor);
        }

        public T sync() {
            return supplier.get();
        }
    }
}
