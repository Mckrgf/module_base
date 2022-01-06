package com.yaobing.module_apt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : yaobing
 * @date : 2020/10/27 16:51
 * @desc :
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Presenter {

    Class[] value();

}
