package com.proxy.base.util

import ex.ss.lib.components.log.SSLog

/**
 * 2024/9/11
 */
object TimeCost {

    private val log by lazy { SSLog.create("TimeCost") }

    fun <T> cost(key: String, block: () -> T): T {
        log.d("$key cost start")
        val start = System.currentTimeMillis()
        val value = block()
        val cost = System.currentTimeMillis() - start
        log.d("$key cost $cost")
        return value
    }

    suspend fun <T> costSuspend (key: String, block: suspend () -> T): T {
        log.d("$key cost start")
        val start = System.currentTimeMillis()
        val value = block()
        val cost = System.currentTimeMillis() - start
        log.d("$key cost $cost")
        return value
    }

}