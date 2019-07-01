package rpcTool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcGenerator {
    private static RpcConnector connector;

    private String headUrl;

    @SuppressWarnings("unchecked")
    <T> T client(Class<T> serviceClass) {
        if (serviceClass.isAnnotationPresent(RpcService.class)) {
            RpcService rpcService = serviceClass.getAnnotation(RpcService.class);
            headUrl = rpcService.path();
            validateUrl(headUrl);
        }
        if (headUrl == null) {
            headUrl = "/rpc/" + serviceClass.getName();
        }
        Class[] classes = new Class[] {serviceClass};
        return (T) Proxy.newProxyInstance(RpcProxy.class.getClassLoader(), classes, new RpcProxy());
    }

    <T> void server(T service) {

    }

    public class RpcProxy implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String tailUrl = null;
            String httpMethod = null;
            if (method.isAnnotationPresent(RpcService.class)) {
                RpcService rpcService = method.getAnnotation(RpcService.class);
                tailUrl = validateUrl(rpcService.path());
                httpMethod = rpcService.method();
            }
            if (tailUrl == null) {
                tailUrl = "/" + method.getName();
            }
            return connector.connect(headUrl + tailUrl, httpMethod, args, method.getReturnType());
        }
    }

    public static String validateUrl(String url) {
        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        return url;
    }
}
