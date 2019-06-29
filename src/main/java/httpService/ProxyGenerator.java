package httpService;

import com.alibaba.fastjson.JSON;
import httpService.annotation.*;
import httpService.util.DefaultRemoteConnector;
import httpService.util.ProxyBox;
import httpService.util.RemoteConnector;
import httpService.util.UrlParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ProxyGenerator {
    private static final RemoteConnector connector = new DefaultRemoteConnector();
    private ExecutorService executor;

    private String headUrl;

    public ProxyGenerator() { }


    public ProxyGenerator(ExecutorService executor) {
        this.executor = executor;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> clazz) {
        if (clazz.isAnnotationPresent(ContentPath.class)) {
            ContentPath rpcService = clazz.getAnnotation(ContentPath.class);
            headUrl = rpcService.path();
        } else {
            headUrl = "";
        }
        Class[] classes = new Class[] {clazz};
        return (T) Proxy.newProxyInstance(
                RpcProxy.class.getClassLoader(),
                classes,
                new RpcProxy());
    }

    @SuppressWarnings("unchecked")
    public class RpcProxy implements InvocationHandler {
        private final Map<Method, ProxyBox> boxMap = new HashMap<>();

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            ProxyBox box = boxMap.get(method);
            if (box == null) {
                box = init(method, args);
            }
            return box.getResponseObjectFunction().apply(box, args);
        }


        private ProxyBox init(Method method, Object[] args) {
            ProxyBox box = new ProxyBox();

            String tailUrl;
            HttpMethod httpMethod;
            if (method.isAnnotationPresent(HttpMapping.class)) {
                HttpMapping rpcService = method.getAnnotation(HttpMapping.class);
                httpMethod = rpcService.method();
                tailUrl = rpcService.path();
                if (!tailUrl.startsWith("/")) {
                    tailUrl = "/" + tailUrl;
                }
            } else  {
                String methodName = method.getName();
                if (methodName.startsWith("get")) {
                    httpMethod = HttpMethod.GET;
                } else if (methodName.startsWith("post")) {
                    httpMethod = HttpMethod.POST;
                } else if (methodName.startsWith("put")) {
                    httpMethod = HttpMethod.PUT;
                } else if (methodName.startsWith("delete")) {
                    httpMethod = HttpMethod.DELETE;
                } else {
                    throw new IllegalArgumentException("无法解析 " + methodName + " 的url");
                }
                int index = httpMethod.name().length();
                char ch = methodName.charAt(index);
                if (ch >= 'A' && ch <= 'Z') {
                    ch += 32;
                }
                tailUrl = "/" + ch + methodName.substring(index + 1);
            }
            box.setMethod(httpMethod);
            UrlParser parser = UrlParser.of(headUrl + tailUrl);

            Object entity = null;
            Map<String, Integer> pathVarMap = new HashMap<>();
            Map<String, Integer> paramIndexMap = new HashMap<>();
            Map<String, Integer> headersIndexMap = new HashMap<>();

            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                for (Annotation annotation : parameter.getAnnotations()) {
                    if (annotation instanceof HttpBody) {
                        if (entity == null) {
                            entity = args[i];
                            box.setEntityIndex(i);
                        } else {
                            throw new IllegalArgumentException("HttpBody 只能存在一个");
                        }
                    } else if (annotation instanceof HttpParam) {

                        String key = ((HttpParam) annotation).name();
                        if ("".equals(key)) {
                            key = parameter.getName();
                        }
                        paramIndexMap.put(key, i);
                    } else if (annotation instanceof HttpHead) {

                        String key = ((HttpHead) annotation).name();
                        if ("".equals(key)) {
                            key = parameter.getName();
                        }
                        headersIndexMap.put(key, i);
                    } else if (annotation instanceof HttpPathVariable) {
                        String name = ((HttpPathVariable) annotation).name();
                        if ("".equals(name)) {
                            name = parameter.getName();
                        }
                        pathVarMap.put(name, i);
                    }
                }
            }

            BiFunction<Map<String, Integer>, Object[], Map<String, String>> biFunction =
                    (map, obj) -> {
                        if (map.isEmpty()) {
                            return null;
                        } else {
                            Map<String, String> paramMap = new HashMap<>(map.size());
                            for (String s : map.keySet()) {
                                paramMap.put(s, (String) obj[map.get(s)]);
                            }
                            return paramMap;
                        }
                    };

            Function<Object[], Map<String, String>> paramsMapFunction =
                    objects -> biFunction.apply(paramIndexMap, objects);
            Function<Object[], Map<String, String>> headersMapFunction =
                    objects -> biFunction.apply(headersIndexMap, objects);
            Function<Object[], String> pathMapFunction =
                    (objects -> parser.parsePath(biFunction.apply(pathVarMap, objects)));

            box.setHeadersMapFunction(headersMapFunction);
            box.setParamsMapFunction(paramsMapFunction);
            box.setUrlMapFunction(pathMapFunction);

            box.setResponseObjectFunction(initResponse(method));
            boxMap.put(method, box);
            return box;
        }

        private BiFunction<ProxyBox, Object[], Object> initResponse(Method method) {

            Class returnClazz = method.getReturnType();
            Type genericReturnType = method.getGenericReturnType();

            if (Future.class.isAssignableFrom(returnClazz)) {
                ParameterizedType returnType = (ParameterizedType) genericReturnType;
                Type finalType = returnType.getActualTypeArguments()[0];
                String typeName = finalType.getTypeName().split("<")[0];
                final Class finalClazz;
                try {
                    finalClazz = Class.forName(typeName);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if (executor == null) {
                    executor = Executors.newFixedThreadPool(20);
                }

                return (box, arguments) -> {
                    Object entity = box.getEntityIndex() == -1 ?
                            null :
                            arguments[box.getEntityIndex()];

                    CompletableFuture<String> future = connector.getAsync(
                            box.getUrlMapFunction().apply(arguments),
                            box.getMethod(),
                            box.getParamsMapFunction().apply(arguments),
                            box.getHeadersMapFunction().apply(arguments),
                            entity,
                            executor);
                    return future.thenApply(res -> {
                        try {
                            return parseObject(res, finalClazz, finalType);
                        } catch (Throwable throwable) {
                            throw new RuntimeException(throwable);
                        }
                    });
                };

            } else {
                return (box, arguments) -> {
                    Object entity = box.getEntityIndex() == -1 ?
                            null :
                            arguments[box.getEntityIndex()];

                    String responseEntity = connector.get(
                            box.getUrlMapFunction().apply(arguments),
                            box.getMethod(),
                            box.getParamsMapFunction().apply(arguments),
                            box.getHeadersMapFunction().apply(arguments),
                            entity);
                    try {
                        return parseObject(responseEntity, returnClazz, genericReturnType);
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                };
            }
        }

        private Object parseObject(
                String responseEntity,
                Class returnClazz,
                Type genericReturnType)
                throws Throwable {

            if (returnClazz == void.class) {
                return null;
            }

            if (returnClazz == String.class) {
                return responseEntity;
            }

            if (returnClazz.isArray()) {
                Class clazz = returnClazz.getComponentType();
                List list = JSON.parseArray(responseEntity, clazz);
                Object[] objects = (Object[]) Array.newInstance(clazz, list.size());
                list.toArray(objects);
                return objects;


            }

            if (Collection.class.isAssignableFrom(returnClazz)) {
                Function<String, List> listFunction;
                if (genericReturnType instanceof ParameterizedType) {
                    ParameterizedType returnType = (ParameterizedType) genericReturnType;
                    Type typeArg = returnType.getActualTypeArguments()[0];
                    Class clazz = Class.forName(typeArg.getTypeName());
                    listFunction = res -> JSON.parseArray(res, clazz);
                } else {
                    listFunction = JSON::parseArray;
                }

                if (returnClazz.isAssignableFrom(List.class)) {
                    return listFunction.apply(responseEntity);
                }
                if (returnClazz.isAssignableFrom(HashSet.class)) {
                    return new HashSet<>(listFunction.apply(responseEntity));
                }
                Constructor constructor = returnClazz.getConstructor(Collection.class);
                return constructor.newInstance(listFunction.apply(responseEntity));
            }

            return JSON.parseObject(responseEntity, returnClazz);
        }
    }
}
