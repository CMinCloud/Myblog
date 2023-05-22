package com.cm.common.aop;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheAnnotation {

    String key() default "";

    String cacheName() default "";

    long ttl() default 60 * 60L;

    TimeUnit timeUnit() default TimeUnit.SECONDS;


}
