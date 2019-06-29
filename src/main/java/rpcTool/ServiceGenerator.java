package rpcTool;

import com.alibaba.fastjson.JSON;
import rpcTool.annotation.HttpBody;
import rpcTool.annotation.HttpHead;
import rpcTool.annotation.HttpParam;
import rpcTool.annotation.HttpRequest;
import rpcTool.annotation.RequestPathVariable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ServiceGenerator {
    private static RemoteConnector connector = new DefaultRemoteConnector();

    private String headUrl;

    @SuppressWarnings("unchecked")
    <T> T client(Class<T> serviceClass) {
        if (serviceClass.isAnnotationPresent(HttpRequest.class)) {
            HttpRequest rpcService = serviceClass.getAnnotation(HttpRequest.class);
            headUrl = rpcService.path();
        } else {
            headUrl = "";
        }
        Class[] classes = new Class[] {serviceClass};
        return (T) Proxy.newProxyInstance(
                RpcProxy.class.getClassLoader(),
                classes,
                new RpcProxy());
    }

    @SuppressWarnings("unchecked")
    public class RpcProxy implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String tailUrl = null;
            String httpMethod = null;
            if (method.isAnnotationPresent(HttpRequest.class)) {
                HttpRequest rpcService = method.getAnnotation(HttpRequest.class);
                httpMethod = rpcService.method();
                tailUrl = rpcService.path();
            }
            if ("".equals(tailUrl)) {
                tailUrl = "/" + method.getName();
            }
            if (httpMethod == null) {
                httpMethod = "post";
            }
            Map<String, String> params = null;
            Map<String, String> headers = null;
            List<String> pathVariableName = null;
            List<String> pathVariableValue = null;
            Object entity = null;
            String finalUrl = headUrl + tailUrl;


            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                for (Annotation annotation : parameter.getAnnotations()) {
                    if (annotation instanceof HttpBody) {
                        if (entity == null) {
                            entity = args[i];
                        } else {
                            throw new IllegalArgumentException();
                        }
                    } else if (annotation instanceof HttpParam) {
                        if (params == null) {
                            params = new HashMap<>();
                        }
                        String key = ((HttpParam) annotation).name();
                        String value = (String) args[i];
                        if ("".equals(key)) {
                            key = parameter.getName();
                        }
                        params.put(key, value);
                    } else if (annotation instanceof HttpHead) {
                        if (headers == null) {
                            headers = new HashMap<>();
                        }
                        String key = ((HttpHead) annotation).name();
                        String value = (String) args[i];
                        if ("".equals(key)) {
                            key = parameter.getName();
                        }
                        headers.put(key, value);
                    } else if (annotation instanceof RequestPathVariable) {
                        if (pathVariableName == null) {
                            pathVariableName = new ArrayList<>();
                            pathVariableValue = new ArrayList<>();
                        }
                        String name = ((RequestPathVariable) annotation).name();
                        if ("".equals(name)) {
                            name = parameter.getName();
                        }
                        pathVariableName.add(name);
                        pathVariableValue.add((String) args[i]);
                    }
                }
            }
            UrlParser parser = UrlParser.of(finalUrl);
            if (parser.pathVariableNumber() > 0) {
                parser.addPathVariable(pathVariableName, pathVariableValue);
            }

            String responseEntity = connector.get(
                    parser.toString(),
                    httpMethod,
                    params,
                    headers,
                    entity);

            Class returnClazz = method.getReturnType();

            if (returnClazz.isArray()) {
                Class clazz = returnClazz.getComponentType();
                List list = JSON.parseArray(responseEntity, clazz);
                Object[] strings = (Object[]) Array.newInstance(clazz, list.size());
                list.toArray(strings);
                return strings;

            } else if (Collection.class.isAssignableFrom(returnClazz)) {
                List list;
                Type type = method.getGenericReturnType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType returnType = (ParameterizedType) type;
                    Type typeArg = returnType.getActualTypeArguments()[0];
                    Class clazz = Class.forName(typeArg.getTypeName());
                    list = JSON.parseArray(responseEntity, clazz);
                } else {
                    if (responseEntity.startsWith("[{")) {
                        list = JSON.parseArray(responseEntity, Map.class);
                    } else {
                        list = JSON.parseArray(responseEntity, String.class);
                    }
                }

                if (returnClazz.isAssignableFrom(list.getClass())) {
                    return list;
                }
                if (returnClazz.isAssignableFrom(HashSet.class)) {
                    return new HashSet<>(list);
                }
                Constructor constructor = returnClazz.getConstructor(Collection.class);
                return constructor.newInstance(list);

            } else {
                return JSON.parseObject(responseEntity, returnClazz);
            }
        }
    }
}
