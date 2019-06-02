package Tools;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MappingException;

import java.io.*;

public class Copy  {

    @Deprecated
    @SuppressWarnings("unchecked")
    public static <T> T copy(T object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);

            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T doCopy(T object) {
        try {
            Mapper mapper = DozerBeanMapperBuilder.buildDefault();
            return (T) mapper.map(object, object.getClass());
        } catch (MappingException e) {
            throw new RuntimeException("object 需要有无参构造函数", e);
        }

    }
}
