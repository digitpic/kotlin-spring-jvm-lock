package com.workspace.jvmlock.common.aop.lock

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class JvmLockAspect {
    private val readWriteLock = ReentrantReadWriteLock(true)
    private val executor = Executors.newSingleThreadExecutor()

    @Around("@annotation(JvmLock)")
    fun lockAround(joinPoint: ProceedingJoinPoint): Any? {
        val method = (joinPoint.signature as MethodSignature).method
        val annotation = method.getAnnotation(JvmLock::class.java)

        val type = annotation.type
        val waitTime = annotation.waitTime.toLong()
        val leaseTime = annotation.leaseTime.toLong()

        val lock = when (type) {
            LockType.READ -> readWriteLock.readLock()
            LockType.WRITE -> readWriteLock.writeLock()
        }

        val acquired = lock.tryLock(waitTime, TimeUnit.SECONDS)
        if (!acquired) {
            throw RuntimeException("락 대기 시간 초과 (waitTime=${waitTime}s)")
        }

        return try {
            val future = executor.submit<Any?> { joinPoint.proceed() }
            future.get(leaseTime, TimeUnit.SECONDS)
        } finally {
            lock.unlock()
            println("락 점유 시간 만료 (${leaseTime}s, type=$type)")
        }
    }
}
