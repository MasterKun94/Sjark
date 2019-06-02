package httpClientBuilder;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

public class RestUtil {
    public static <T> T toObject(InputStream inputStream, Charset charset, Class<T> clazz) throws IOException {
        return JSON.parseObject(IOUtils.toString(inputStream, charset), clazz);
    }

    public static <T> T toObject(String string, Class<T> clazz) {
        return JSON.parseObject(string, clazz);
    }

    public static <T> List<T> toArray(InputStream inputStream, Charset charset, Class<T> clazz) throws IOException {
        return JSON.parseArray(IOUtils.toString(inputStream, charset), clazz);
    }

    public static <T> List<T> toArray(String string, Class<T> clazz) {
        return JSON.parseArray(string, clazz);
    }

    public static String toString(InputStream inputStream, Charset charset) throws IOException {
        return IOUtils.toString(inputStream, charset);
    }
}
