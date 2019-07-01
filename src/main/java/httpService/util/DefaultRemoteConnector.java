package httpService.util;

import com.alibaba.fastjson.JSON;
import httpService.HttpMethod;
import httpService.builder.HttpBuilder;
import httpService.builder.HttpConnector;
import org.apache.http.util.EntityUtils;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class DefaultRemoteConnector implements RemoteConnector {

    @Override
    public String get(
            String url,
            HttpMethod method,
            Map<String, String> param,
            Map<String, String> headers,
            Object entity) {

        return createRequest(url, method, param, headers, entity).sync();
    }

    @Override
    public CompletableFuture<String> getAsync(
            String url,
            HttpMethod method,
            Map<String, String> param,
            Map<String, String> headers,
            Object entity,
            ExecutorService executor) {
        return createRequest(url, method, param, headers, entity).async(executor);
    }

    private HttpBuilder.HttpResponseBuilder<String> createRequest(
            String url,
            HttpMethod method,
            Map<String, String> param,
            Map<String, String> headers,
            Object entity) {

        HttpBuilder builder;
        switch (method) {
            case GET:
                builder = HttpBuilder.get(url);
                break;
            case POST:
                builder = HttpBuilder.post(url);
                break;
            case PUT:
                builder = HttpBuilder.put(url);
                break;
            case DELETE:
                builder = HttpBuilder.delete(url);
                break;
            default:
                throw new IllegalArgumentException();
        }
        builder.rest();
        if (param != null) {
            builder.params(param);
        }
        if (headers != null) {
            builder.headers(headers);
        }
        if (entity != null) {
            builder.entity(JSON.toJSONString(entity));
        }

        return builder.execute(HttpConnector.of((
                (req, res) -> EntityUtils.toString(res.getEntity()))));
    }
}
