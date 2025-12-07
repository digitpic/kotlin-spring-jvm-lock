package com.workspace.jvmlock.coupon.service

import com.workspace.jvmlock.coupon.entity.Coupon
import com.workspace.jvmlock.coupon.repository.CouponRepository
import java.util.concurrent.locks.ReentrantLock
import org.springframework.stereotype.Service

@Service
class CouponService(
    private val repository: CouponRepository,
) {
    private val lock = ReentrantLock(true)

    fun issue(userId: Long) {
        val coupon = Coupon(userId)
        lock.lock()
        try {
            if (repository.findAll().size >= MAX_COUPON_COUNT) {
                throw IllegalStateException("쿠폰이 모두 소진되었습니다.")
            }
            repository.create(coupon)
        } finally {
            lock.unlock()
        }
    }

    companion object {
        private const val MAX_COUPON_COUNT = 100
    }
}
