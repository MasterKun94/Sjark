package httpService.util;

import httpService.HttpMethod;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface RemoteConnector {
    String get(
            String url,
            HttpMethod method,
            Map<String, String> param,
            Map<String, String> headers,
            Object entity);

    CompletableFuture<String> getAsync(
            String url,
            HttpMethod method,
            Map<String, String> param,
            Map<String, String> headers,
            Object entity,
            ExecutorService executor);
}
