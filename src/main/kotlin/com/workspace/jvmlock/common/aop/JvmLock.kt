package com.workspace.jvmlock.common.aop

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class JvmLock(
    val waitTime: Int = 3,
    val leaseTime: Int = 3,
)
