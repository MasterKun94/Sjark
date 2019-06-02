package httpClientBuilder.connector;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public interface ResponseHandler<T> {

    T  handleResponse(HttpResponse response) throws IOException;

    T catchException(Exception e);
}
