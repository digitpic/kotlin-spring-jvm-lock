package com.workspace.jvmlock.coupon.service

import com.workspace.jvmlock.coupon.entity.Coupon
import com.workspace.jvmlock.coupon.repository.CouponRepository
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CouponTest {
    private val repository = CouponRepository()
    private val service = CouponService(repository)

    @Test
    fun `쿠폰 발급에 성공한다`() {
        service.issue(1L)
        val coupons: List<Coupon> = repository.findByUserId(1L)
        assertThat(coupons.size).isEqualTo(1)
        assertThat(coupons[0].userId).isEqualTo(1L)
    }

    @Test
    fun `200명이 동시에 요청을 보내면 쿠폰이 100개만 발급된다`() {
        val threadCount = 200
        val maxCouponCount = 100

        val latch = CountDownLatch(1)
        val done = CountDownLatch(threadCount)

        val executor = Executors.newFixedThreadPool(threadCount)

        repeat(threadCount) {
            executor.submit {
                latch.await()
                try {
                    service.issue(it.toLong())
                } finally {
                    done.countDown()
                }
            }
        }

        latch.countDown()
        done.await()
        executor.shutdown()

        val coupons = repository.findAll()

        assertThat(coupons.size).isEqualTo(maxCouponCount)
    }
}

