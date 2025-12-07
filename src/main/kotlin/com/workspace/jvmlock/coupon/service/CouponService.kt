package com.workspace.jvmlock.coupon.service

import com.workspace.jvmlock.coupon.entity.Coupon
import com.workspace.jvmlock.coupon.repository.CouponRepository
import org.springframework.stereotype.Service

@Service
class CouponService(
    private val repository: CouponRepository,
) {
    fun issue(userId: Long) {
        val coupon = Coupon(userId)
        repository.create(coupon)
    }
}

