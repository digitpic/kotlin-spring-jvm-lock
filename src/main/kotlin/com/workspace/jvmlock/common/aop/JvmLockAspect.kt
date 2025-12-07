package com.workspace.jvmlock.common.aop

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class JvmLockAspect {
    private val lock = ReentrantLock(true)
    private val executor = Executors.newCachedThreadPool()

    @Around("@annotation(JvmLock)")
    fun lockAround(joinPoint: ProceedingJoinPoint): Any? {
        val method = (joinPoint.signature as MethodSignature).method
        val annotation = method.getAnnotation(JvmLock::class.java)

        val waitTime = annotation.waitTime.toLong()
        val leaseTime = annotation.leaseTime.toLong()

        val acquired = lock.tryLock(waitTime, TimeUnit.SECONDS)
        if (!acquired) {
            throw RuntimeException("락 대기 시간 초과 (waitTime=${waitTime}s)")
        }

        return try {
            val future = executor.submit<Any?> { joinPoint.proceed() }
            future.get(leaseTime, TimeUnit.SECONDS)
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }
}
