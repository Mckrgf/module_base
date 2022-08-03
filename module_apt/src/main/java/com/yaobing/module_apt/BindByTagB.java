package com.yaobing.module_apt;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yaobing
 */

@Target(FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface BindByTagB {
    /** View ID to which the field will be bound. */
    String value();
}