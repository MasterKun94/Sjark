package rpcTool;

import jdk.internal.jline.internal.Nullable;

public class DefaultRpcConnector implements RpcConnector {
    @Override
    public <IN, OUT> OUT connect(String url, @Nullable String method, IN in, Class<OUT> outClass) {
        return null;//TODO
    }
}
