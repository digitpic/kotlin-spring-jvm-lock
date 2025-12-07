package com.workspace.jvmlock.coupon.repository

import com.workspace.jvmlock.coupon.entity.Coupon
import java.util.concurrent.ConcurrentHashMap
import org.springframework.stereotype.Repository

@Repository
class CouponRepository {
    private val db = ConcurrentHashMap<Long, MutableList<Coupon>>()

    fun create(coupon: Coupon): Coupon {
        db.computeIfAbsent(coupon.userId) { mutableListOf() }.add(coupon)
        return coupon
    }

    fun findByUserId(userId: Long): List<Coupon> {
        return db[userId] ?: emptyList()
    }

    fun findAll(): List<Coupon> {
        return db.values.flatten()
    }
}

