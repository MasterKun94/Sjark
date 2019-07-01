package rpcTool;

import com.sun.istack.internal.Nullable;

public class DefaultRpcConnector implements RpcConnector {
    @Override
    public <IN, OUT> OUT connect(String url, @Nullable String method, IN in, Class<OUT> outClass) {
        return null;//TODO
    }
}
