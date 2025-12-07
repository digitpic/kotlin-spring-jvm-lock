package com.workspace.jvmlock.coupon.service

import com.workspace.jvmlock.coupon.entity.Coupon
import com.workspace.jvmlock.coupon.repository.CouponRepository
import org.springframework.stereotype.Service

@Service
class CouponService(
    private val repository: CouponRepository,
) {
    private val lock = Any()

    fun issue(userId: Long) {
        val coupon = Coupon(userId)
        synchronized(lock) {
            if (repository.findAll().size >= MAX_COUPON_COUNT) {
                throw IllegalStateException("쿠폰이 모두 소진되었습니다.")
            }
            repository.create(coupon)
        }
    }

    companion object {
        private const val MAX_COUPON_COUNT = 100
    }
}

