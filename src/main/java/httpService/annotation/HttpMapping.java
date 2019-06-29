package httpService.annotation;

import httpService.HttpMethod;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpMapping {
    String path() default "";

    HttpMethod method();
}
