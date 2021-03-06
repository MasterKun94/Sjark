package httpService.connector.builder;

import org.apache.commons.codec.Charsets;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 一个http请求建造器
 */
public class HttpBuilder {

    private HttpRequestBase request;
    private StringBuilder urlBuilder;
    private boolean haveParam;

    public HttpBuilder(HttpRequestBase request, String url) {
        this.request = request;
        this.urlBuilder = new StringBuilder();
//        if (url.startsWith("http://")) {
            urlBuilder.append(url);
//        } else {
//            urlBuilder.append("http://").append(url);
//        }
    }

    private static HttpBuilder start(HttpRequestBase request, String url) {
        return new HttpBuilder(request, url);
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
        if (params != null && !params.isEmpty()) {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                param(key, params.get(key));
            }
        }
        return this;
    }

    public HttpBuilder param(String key, String value) {
        if (value != null && !"".equals(value)) {
            urlBuilder.append(haveParam ? "&" : "?")
                    .append(key).append("=").append(value);
            haveParam = true;
        }
        return this;
    }

    public HttpBuilder headers(Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            Set<String> keys = headers.keySet();
            keys.forEach(key -> request.setHeader(key, headers.get(key)));
        }
        return this;
    }

    public HttpBuilder header(String head, String value) {
        if (value != null && !"".equals(value)) {
            request.setHeader(head, value);
        }
        return this;
    }

    public HttpBuilder rest() {
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        return this;
    }

    public HttpBuilder entity(String entity) {
        if (entity != null) {
            if (request instanceof HttpPost || request instanceof HttpPut) {
                ((HttpEntityEnclosingRequestBase) request)
                        .setEntity(new StringEntity(entity, Charsets.UTF_8));
            } else {
                throw new UnsupportedOperationException(
                        request.getMethod() + " method not supported");
            }
        }
        return this;
    }

    public HttpBuilder charset(Charset charset) {
        request.setHeader("Charset", charset.toString());
        return this;
    }

    public <T> HttpResponseBuilder<T> execute(HttpConnector<T> connector) {
        request.setURI(URI.create(urlBuilder.toString()));
        return new HttpResponseBuilder<>(() -> connector.execute(request));
    }

    public <T> HttpResponseBuilder<T> execute(Function<String, T> handler) {
        request.setURI(URI.create(urlBuilder.toString()));
        HttpConnector<T> connector = HttpConnector.of((req, res) ->
                handler.apply(EntityUtils.toString(res.getEntity())));
        return new HttpResponseBuilder<>(() -> connector.execute(request));
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
