package httpService.util;

import httpService.HttpMethod;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface RemoteConnector {
    <IN> String get(
            String url,
            HttpMethod method,
            Map<String, String> param,
            Map<String, String> headers,
            IN entity);

    <IN> CompletableFuture<String> getAsync(
            String url,
            HttpMethod method,
            Map<String, String> param,
            Map<String, String> headers,
            IN entity,
            ExecutorService executor);
}
