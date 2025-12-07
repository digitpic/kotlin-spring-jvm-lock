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
    private val scheduler = Executors.newSingleThreadScheduledExecutor()

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

        val unlockTask = scheduler.schedule({
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
                println("락 점유 시간 만료 (${leaseTime}s)")
            }
        }, leaseTime, TimeUnit.SECONDS)

        return try {
            joinPoint.proceed()
        } finally {
            unlockTask.cancel(false)
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }
}
