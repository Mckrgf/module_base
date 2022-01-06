package com.yaobing.module_apt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wangshizhan on 16/12/30.
 * 多个viewCode 以“，”隔开
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Router {

    String DEFAULT = "unknown";
    String value();
    String viewCode() default DEFAULT;
}
