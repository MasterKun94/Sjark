package httpService;

import com.alibaba.fastjson.JSON;
import httpService.annotation.ContentPath;
import httpService.annotation.HttpBody;
import httpService.annotation.HttpHead;
import httpService.annotation.HttpMapping;
import httpService.annotation.HttpParam;
import httpService.annotation.HttpPathVariable;
import httpService.util.DefaultRemoteConnector;
import httpService.util.RemoteConnector;
import httpService.util.UrlParser;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * http请求代理生成器，
 */
public class HttpProxyGenerator {
    private static final RemoteConnector connector = new DefaultRemoteConnector();
    private final InvocationHandler httpProxy = new HttpProxy();
    private ExecutorService executor;
    private String headUrl;

    public HttpProxyGenerator() { }


    /**
     * 如果被代理的接口有方法返回{@code Future}类型的话建议使用此构造器实例化对象，异步线程会在
     * 给定的{@code executor}线程池中运行，如果不适用此构造器，那么也会自动生成一个线程池：
     * {@code Executors.newFixedThreadPool(20)} 供{@code Future} 的线程使用
     *
     * @param executor 供异步线程运行的线程池
     */
    public HttpProxyGenerator(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * 生成一个代理对象，参数是一个接口，且通过{@link httpService.annotation} 中的注解标注
     * 请求类型，url，参数，头部信息，请求体等信息代理生成器会根据注解自动生成一个代理的对象
     *
     * @param clazz 被代理的接口的class对象
     * @param <T> 代理对象的类型
     * @return 一个实例化的代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> clazz) {
        if (clazz.isAnnotationPresent(ContentPath.class)) {
            ContentPath rpcService = clazz.getAnnotation(ContentPath.class);
            headUrl = rpcService.value();
            if (headUrl.startsWith("http://")) {
                headUrl = headUrl.substring("http://".length());
            }
        } else {
            headUrl = "";
        }
        Class[] classes = new Class[] {clazz};
        return (T) Proxy.newProxyInstance(
                HttpProxy.class.getClassLoader(),
                classes,
                httpProxy);
    }

    @SuppressWarnings("unchecked")
    private class HttpProxy implements InvocationHandler {
        private Map<Method, Function<Object[], Object>> methodMap = new HashMap<>();

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return methodMap.computeIfAbsent(method, k -> init(method)).apply(args);
        }

        //第一次调对象的某个方法时会调用该方法，并将该方法返回的函数放入{@code methodMap}中，
        private Function<Object[], Object> init(Method method) {

            String tailUrl;
            HttpMethod httpMethod;
            if (method.isAnnotationPresent(HttpMapping.class)) {
                HttpMapping rpcService = method.getAnnotation(HttpMapping.class);
                httpMethod = rpcService.method();
                tailUrl = rpcService.value();
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
            UrlParser parser = UrlParser.of(headUrl + tailUrl);

            Map<String, Integer> pathVarMap = new HashMap<>();
            Map<String, Integer> paramIndexMap = new HashMap<>();
            Map<String, Integer> headersIndexMap = new HashMap<>();
            int entityIndex = -1;
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                for (Annotation annotation : parameter.getAnnotations())
                    if (annotation instanceof HttpBody) {
                        if (entityIndex == -1) {
                            entityIndex = i;
                        } else {
                            throw new IllegalArgumentException("HttpBody 只能存在一个");
                        }
                    } else if (annotation instanceof HttpParam) {

                        String key = ((HttpParam) annotation).value();
                        if ("".equals(key)) {
                            key = parameter.getName();
                        }
                        paramIndexMap.put(key, i);
                    } else if (annotation instanceof HttpHead) {

                        String key = ((HttpHead) annotation).value();
                        if ("".equals(key)) {
                            key = parameter.getName();
                        }
                        headersIndexMap.put(key, i);
                    } else if (annotation instanceof HttpPathVariable) {
                        String name = ((HttpPathVariable) annotation).value();
                        if ("".equals(name)) {
                            name = parameter.getName();
                        }
                        pathVarMap.put(name, i);
                    }
            }

            BiFunction<Map<String, Integer>, Object[], Map<String, String>> getArgByIdx =
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

            Function<Object[], Map<String, String>> paramsMapFunction = methodArgs ->
                    getArgByIdx.apply(paramIndexMap, methodArgs);
            Function<Object[], Map<String, String>> headersMapFunction = methodArgs ->
                    getArgByIdx.apply(headersIndexMap, methodArgs);
            Function<Object[], String> pathMapFunction = methodArgs ->
                    parser.parsePath(getArgByIdx.apply(pathVarMap, methodArgs));

            return initProxyMethodFunction(
                    method,
                    entityIndex,
                    httpMethod,
                    headersMapFunction,
                    paramsMapFunction,
                    pathMapFunction
            );
        }

        //根据解析注解得到的参数返回代理方法的函数
        private Function<Object[], Object> initProxyMethodFunction(
                Method method,
                int entityIndex,
                HttpMethod httpMethod,
                Function<Object[], Map<String, String>> headersMapFunction,
                Function<Object[], Map<String, String>> paramsMapFunction,
                Function<Object[], String> pathFunction) {

            Class returnClazz = method.getReturnType();
            Type genericReturnType = method.getGenericReturnType();
            Function<String, Object> objectParser;
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
                objectParser = parseObject(finalClazz, finalType);

                return arguments -> connector.getAsync(
                        pathFunction.apply(arguments),
                        httpMethod,
                        paramsMapFunction.apply(arguments),
                        headersMapFunction.apply(arguments),
                        entityIndex == -1 ? null : arguments[entityIndex],
                        executor)
                        .thenApply(res -> {
                            try {
                                return objectParser.apply(res);
                            } catch (Throwable throwable) {
                                throw new RuntimeException(throwable);
                            }
                        });
            } else {
                objectParser = parseObject(returnClazz, genericReturnType);
                return arguments -> {
                    String responseEntity = connector.get(
                            pathFunction.apply(arguments),
                            httpMethod,
                            paramsMapFunction.apply(arguments),
                            headersMapFunction.apply(arguments),
                            entityIndex == -1 ? null : arguments[entityIndex]);
                    return objectParser.apply(responseEntity);
                };
            }
        }

        //根据代理方法的返回类型得到解析对象的函数
        private Function<String, Object> parseObject(
                Class returnClazz,
                Type genReturnType) {

            if (returnClazz == void.class) {
                return str -> null;
            }

            if (returnClazz == String.class) {
                return str -> str;
            }

            if (returnClazz.isArray()) {
                Class clazz = returnClazz.getComponentType();
                return str -> {
                    List list = JSON.parseArray(str, clazz);
                    Object[] objects = (Object[]) Array.newInstance(clazz, list.size());
                    list.toArray(objects);
                    return objects;
                };
            }

            if (Collection.class.isAssignableFrom(returnClazz)) {
                Function<String, List> listFunction;
                if (genReturnType instanceof ParameterizedType) {
                    ParameterizedType returnType = (ParameterizedType) genReturnType;
                    Type typeArg = returnType.getActualTypeArguments()[0];
                    try {
                        Class clazz = Class.forName(typeArg.getTypeName());
                        listFunction = res -> JSON.parseArray(res, clazz);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    listFunction = JSON::parseArray;
                }
                if (returnClazz.isAssignableFrom(ArrayList.class)) {
                    return listFunction.andThen(c -> c);
                }
                if (returnClazz.isAssignableFrom(HashSet.class)) {
                    return listFunction.andThen(HashSet::new);
                }

                for (Constructor constructor : returnClazz.getConstructors()) {
                    if (constructor.getParameterCount() == 1) {
                        Class clazz = constructor.getParameterTypes()[0];
                        if (clazz.isAssignableFrom(List.class)) {
                            return listFunction.andThen(collection -> {
                                try {
                                    return constructor.newInstance(collection);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                    }
                }
            }
            return res -> JSON.parseObject(res, returnClazz);
        }
    }
}
