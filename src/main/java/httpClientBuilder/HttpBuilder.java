package httpClientBuilder;

import com.alibaba.fastjson.JSON;
import httpClientBuilder.connector.HttpConnector;
import org.apache.commons.codec.Charsets;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class HttpBuilder {
    private static final String EQ = "=";
    private static final String AND = "&";

    private Charset charset;
    private HttpRequestBase request;
    private String URL;

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
        if (MapUtils.isEmpty(params)) {
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
        if (MapUtils.isEmpty(headers)) {
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

    public HttpBuilder entity(Object object) {
        String entity = object instanceof String ? (String) object : JSON.toJSONString(object);
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
        this.charset = charset;
        return this;
    }

    public <T> HttpResponseBuilder<T> execute(HttpConnector<T> connector) {
        request.setURI(URI.create(URL));
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

    private String getStringEntity(InputStream stream) {
        try {
            return IOUtils.toString(stream, charset == null ? Charset.defaultCharset() : charset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}