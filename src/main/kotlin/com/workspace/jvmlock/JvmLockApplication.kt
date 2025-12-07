package com.workspace.jvmlock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JvmLockApplication

fun main(args: Array<String>) {
    runApplication<JvmLockApplication>(*args)
}
