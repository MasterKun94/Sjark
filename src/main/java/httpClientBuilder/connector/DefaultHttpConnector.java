package httpClientBuilder.connector;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class DefaultHttpConnector extends HttpConnector<String> {

    @Override
    public String handleResponse(HttpRequest request, HttpResponse response) throws IOException {
        return EntityUtils.toString(response.getEntity());
    }
}
