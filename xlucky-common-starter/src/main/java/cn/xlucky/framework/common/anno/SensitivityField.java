package cn.xlucky.framework.common.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 类说明
 * @author xlucky
 * @date 2020/6/30
 * @version 1.0.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitivityField {
    String format() default "******";
}
