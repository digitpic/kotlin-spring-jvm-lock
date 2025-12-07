package com.workspace.jvmlock.common.aop.lock

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class JvmLock(
    val type: LockType = LockType.WRITE,
    val waitTime: Int = 3,
    val leaseTime: Int = 3,
)
