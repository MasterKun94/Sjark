package httpService.util;

import httpService.HttpMethod;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ProxyBox {
    private Function<Object[], Map<String, String>> paramsMapFunction;
    private Function<Object[], Map<String, String>> headersMapFunction;
    private Function<Object[], String> urlMapFunction;
    private Integer entityIndex = -1;
    private HttpMethod method;

    private BiFunction<ProxyBox, Object[], Object> responseObjectFunction;

    public Function<Object[], Map<String, String>> getParamsMapFunction() {
        return paramsMapFunction;
    }

    public Function<Object[], Map<String, String>> getHeadersMapFunction() {
        return headersMapFunction;
    }

    public Integer getEntityIndex() {
        return entityIndex;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setParamsMapFunction(Function<Object[], Map<String, String>> paramsMap) {
        this.paramsMapFunction = paramsMap;
    }

    public void setHeadersMapFunction(Function<Object[], Map<String, String>> headersMap) {
        this.headersMapFunction = headersMap;
    }

    public void setEntityIndex(Integer entityIndexMap) {
        this.entityIndex = entityIndexMap;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public Function<Object[], String> getUrlMapFunction() {
        return urlMapFunction;
    }

    public void setUrlMapFunction(Function<Object[], String> urlMapFunction) {
        this.urlMapFunction = urlMapFunction;
    }

    public BiFunction<ProxyBox, Object[], Object> getResponseObjectFunction() {
        return responseObjectFunction;
    }

    public void setResponseObjectFunction(BiFunction<ProxyBox, Object[], Object> responseObjectFunction) {
        this.responseObjectFunction = responseObjectFunction;
    }
}
