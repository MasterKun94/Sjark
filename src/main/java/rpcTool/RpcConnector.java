package rpcTool;

import jdk.internal.jline.internal.Nullable;

public interface RpcConnector {
    <IN, OUT> OUT connect(String url, @Nullable String method, IN in, Class<OUT> outClass);
}
