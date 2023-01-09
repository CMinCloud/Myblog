package com.cm.common.aop;


import java.lang.annotation.*;

@Target(value = ElementType.METHOD)  // 指定该注解只能使用在方法前
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {

    //    注解参数：模块名称
    String module() default "";

    //    竹节参属：操作
    String operation() default "";


}
