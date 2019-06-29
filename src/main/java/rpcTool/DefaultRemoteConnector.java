package rpcTool;

import com.alibaba.fastjson.JSON;
import httpClientBuilder.HttpBuilder;
import httpClientBuilder.HttpConnector;
import org.apache.http.util.EntityUtils;

import java.util.Map;

public class DefaultRemoteConnector implements RemoteConnector {

    @Override
    public <IN> String get(
            String url,
            String method,
            Map<String, String> param,
            Map<String, String> headers,
            IN entity) {

        return getStringEntity(url, method, param, headers, entity);
    }


    private <IN> String getStringEntity(
            String url,
            String method,
            Map<String, String> param,
            Map<String, String> headers,
            IN entity) {

        HttpBuilder builder;
        switch (method.toLowerCase()) {
            case "get":
                builder = HttpBuilder.get(url);
                break;
            case "post":
                builder = HttpBuilder.post(url);
                break;
            case "put":
                builder = HttpBuilder.put(url);
                break;
            case "delete":
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
        return builder
                .execute(HttpConnector.of((
                    (request, response) -> EntityUtils.toString(response.getEntity()))))
                .sync();
    }
}
