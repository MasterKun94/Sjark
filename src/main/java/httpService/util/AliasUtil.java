package httpService.util;

import httpService.annotation.Alias;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AliasUtil {
    public static  Object parse(Annotation annotation, String method) {
        Class<? extends Annotation> clazz = annotation.annotationType();
        try {
            Method requestMethod = clazz.getMethod(method);
            Object defaultValue = requestMethod.getDefaultValue();
            Object anotherValue = requestMethod.invoke(annotation);
            if (!anotherValue.equals(defaultValue)) {
                return anotherValue;
            }
            for (Method method1 : clazz.getMethods()) {
                if (method1.isAnnotationPresent(Alias.class)) {
                    if (method1.getAnnotation(Alias.class).value().equals(method)) {
                        anotherValue = method1.invoke(annotation);
                        if (!anotherValue.equals(defaultValue) &&
                                !anotherValue.equals(method1.getDefaultValue())) {
                            return anotherValue;
                        }
                    }
                }
            }
            return defaultValue;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
