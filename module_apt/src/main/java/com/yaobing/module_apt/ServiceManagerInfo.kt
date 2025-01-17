package com.yaobing.module_apt

/**
 * 用于动态配置
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ServiceManagerInfo(val packageName:String)
