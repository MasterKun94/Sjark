package httpClientBuilder;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public interface ResponseHandler<T> {

    T handleResponse(HttpRequest request, HttpResponse response) throws IOException;

    default T handleException(HttpRequest request, Exception e) {
        e.printStackTrace();
        return null;
    }
}
