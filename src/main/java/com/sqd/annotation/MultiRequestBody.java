package com.sqd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sqd
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiRequestBody {

    /**
     * 解析时用到的 JSON 中的 key
     */
    String value() default "";

    /**
     * 是否必传的参数
     */
    boolean required() default true;

    /**
     * 当 value 的值或者参数名不匹配时，是否允许解析最外层属性得到该对象
     */
    boolean parseAllFields() default true;

}
