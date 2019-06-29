package rpcTool;

import java.util.Map;

public interface RemoteConnector {
    <IN> String get(
            String url,
            String method,
            Map<String, String> param,
            Map<String, String> headers,
            IN entity);
}
