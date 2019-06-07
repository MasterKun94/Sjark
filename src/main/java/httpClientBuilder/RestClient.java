package httpClientBuilder;

import httpClientBuilder.connector.HttpConnector;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.function.Function;

public class RestClient {

    public static <T> T get(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            Charset charset,
            Class<T> clazz,
            HttpConnector<T> connector) {
        return connect(params, headers, charset, clazz, connector)
                .apply(HttpBuilder.get(url));
    }

    public static <T> T post(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            String entity,
            Charset charset,
            Class<T> clazz,
            HttpConnector<T> connector) {
        return connect(params, headers, entity, charset, clazz, connector)
                .apply(HttpBuilder.post(url));
    }

    public static <T> T put(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            String entity,
            Charset charset,
            Class<T> clazz,
            HttpConnector<T> connector) {
        return connect(params, headers, entity, charset, clazz, connector)
                .apply(HttpBuilder.put(url));
    }

    public static <T> T delete(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            Charset charset,
            Class<T> clazz,
            HttpConnector<T> connector) {
        return connect(params, headers, charset, clazz, connector)
                .apply(HttpBuilder.delete(url));
    }

    private static <T> Function<HttpBuilder, T> connect(
            Map<String, String> params,
            Map<String, String> headers,
            String entity,
            Charset charset,
            Class<T> clazz,
            HttpConnector<T> connector)
    {
        return httpBuilder -> httpBuilder
                .rest()
                .charset(charset)
                .params(params)
                .headers(headers)
                .entity(entity)
                .execute(connector)
                .sync();
    }

    private static <T> Function<HttpBuilder, T> connect(
            Map<String, String> params,
            Map<String, String> headers,
            Charset charset,
            Class<T> clazz,
            HttpConnector<T> connector)
    {
        return httpBuilder -> httpBuilder
                .rest()
                .charset(charset)
                .params(params)
                .headers(headers)
                .execute(connector)
                .sync();
    }
}
