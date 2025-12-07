package com.workspace.jvmlock.coupon.controller

import com.workspace.jvmlock.coupon.service.CouponService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CouponController(
    private val service: CouponService,
) {
    @PostMapping("/api/v1/users/{id}/coupons/issue")
    fun issue(@PathVariable("id") userId: Long) {
        service.issue(userId)
    }
}

