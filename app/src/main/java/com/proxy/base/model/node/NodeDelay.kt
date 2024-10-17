package com.proxy.base.model.node

import com.proxy.base.config.NodeConfig
import com.proxy.base.proxy.ProxyNodeState
import com.proxy.base.util.proxy.ProxyTest
import com.proxy.base.util.proxy.TCPItem
import ex.ss.lib.components.log.SSLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 2024/9/11
 */

data class DelayResult(
    val list: List<ProxyNodeState>,
    val finish: Boolean,
    val isFirst: Boolean = false,
)

object NodeDelay {

    private val log by lazy { SSLog.create("NodeDelay") }

    internal suspend fun test(
        dataList: List<ProxyNodeState>,
        onResult: suspend (DelayResult) -> Unit,
    ) = withContext(Dispatchers.IO) {
        val list = updateDelay(dataList, NodeConfig.nodeDelay(), 0)
        onResult.invoke(DelayResult(list, false))
        launch(Dispatchers.IO) {
            val asyncList = list.toMutableList().onEach {
                log.d("ASYNC Cache: ${it.url} ,${it.name} , ${it.delay}")
            }
            val indexMap = mutableMapOf<String, Int>().apply {
                asyncList.onEachIndexed { index, state -> put(state.url, index) }
            }
            testNodeDelay(asyncList) {
                log.d("ASYNC Result: ${it.first} , ${it.second}")
                indexMap[it.first]?.also { index ->
                    asyncList[index] = asyncList[index].copy(delay = it.second)
                    onResult.invoke(DelayResult(asyncList, false))
                }
            }
            onResult.invoke(DelayResult(asyncList, true, isFirstTest()))
        }
    }

    @Synchronized
    private fun isFirstTest(): Boolean {
        val alreadyTest = NodeConfig.isFirstTest
        if (alreadyTest) NodeConfig.isFirstTest = false
        return alreadyTest
    }

    private fun updateDelay(
        list: List<ProxyNodeState>,
        delayMap: Map<String, Int>,
        defaultDelay: Int = ProxyTest.TIME_OUT_DELAY,
    ): List<ProxyNodeState> {
        return list.map {
            val delay = delayMap[it.url] ?: defaultDelay
            it.copy(delay = delay)
        }
    }

    private suspend fun testNodeDelay(
        dataList: List<ProxyNodeState>,
        onEach: suspend (Pair<String, Int>) -> Unit = {},
    ): Map<String, Int> {
        val list = dataList.map { TCPItem(it.url, it.address, it.port) }
        if (list.isEmpty()) return mapOf()
        return ProxyTest.delay(5000, list, ProxyTest.TYPE_TCP, onEach).apply {
            NodeConfig.saveNodeDelay(this)
        }
    }

}