package httpService.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来定义识该注解的类的所有请求url的前缀部分，可以用来定义域名，ip地址和端口号等
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentPath {

    /**
     * 定义请求url的前缀部分，可以写成url模板，并在方法参数中添加
     * {@code @HttpPathVariable}
     *
     */
    String value() default "";
}
