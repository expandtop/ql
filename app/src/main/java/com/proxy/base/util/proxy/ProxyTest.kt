package com.proxy.base.util.proxy

import ex.ss.lib.components.log.SSLog
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

/**
 * 2023/12/11
 */
object ProxyTest {


    const val TYPE_TCP = 0
    const val TYPE_ICMP = 1
    const val TYPE_PING = 2
    const val TIME_OUT_DELAY = Short.MAX_VALUE * 2

    private val log by lazy { SSLog.create("ProxyTest") }
    private val delayTestDeferredList = mutableListOf<Deferred<*>>()

    suspend fun delay(
        timeout: Int = 3000,
        list: List<TCPItem>,
        type: Int = TYPE_TCP,
        onEach: suspend (Pair<String, Int>) -> Unit = {},
    ): Map<String, Int> {
        return withContext(Dispatchers.IO) {
            val result = mutableMapOf<String, Int>()
            DelayTestUtil.closeAllTcpSockets()
            delayTestDeferredList.onEach { it.cancel() }
            delayTestDeferredList.clear()
            log.d("type:$type , start")
            list.onEach { node ->
                delayTestDeferredList.add(async {
                    val delayTime = delay(timeout, node, type)
                    onEach.invoke(node.key to delayTime)
                    result.put(node.key, delayTime)
                })
            }
            log.d("type:$type , ${delayTestDeferredList.size}")
            delayTestDeferredList.onEach { it.await() }
            log.d("type:$type , end")
            result
        }
    }

    suspend fun delay(timeout: Int = 3000, node: TCPItem, type: Int = TYPE_TCP): Int {
        return withContext(Dispatchers.IO) {
            log.d("type:$type, start ${node.address} , ${node.port}")
            val time = when (type) {
                TYPE_ICMP -> DelayTestUtil.icmp(node.address, node.port, timeout)
                TYPE_PING -> DelayTestUtil.ping(node.address, node.port, timeout)
                else -> DelayTestUtil.tcping(node.address, node.port, timeout)
            }
            if (time <= -1) TIME_OUT_DELAY else time
        }
    }

}


data class TCPItem(val key: String, val address: String, val port: Int)