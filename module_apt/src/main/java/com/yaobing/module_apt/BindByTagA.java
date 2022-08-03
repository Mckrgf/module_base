package com.yaobing.module_apt;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yaobing
 */

@Target(FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface BindByTagA {
    /** View ID to which the field will be bound. */
    String value();
}