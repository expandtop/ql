package com.proxy.base.model.node

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proxy.base.config.NodeConfig
import com.proxy.base.proxy.ProxyNodeState
import ex.ss.lib.components.log.SSLog
import java.util.concurrent.atomic.AtomicBoolean

data class NodeStorageResult(val list: List<ProxyNodeState>?, val resetSelect: Boolean = false)

object NodeStorage {

    private val log by lazy { SSLog.create("NodeStorage") }
    val nodeResultLiveData = MutableLiveData(NodeStorageResult(null))
    private val gson by lazy { Gson() }
    private val isLoading = AtomicBoolean(false)

    suspend fun featNodeList() = runCatching {
        if (isLoading.compareAndSet(false, true)) {
            val proxies = NodeSubscribe.getSubscribe() //解析数据
            val parseProxies = proxies.map { ProxyNodeState.create(it) }.filter {
                !NodeSubscribe.nodeBlack.any { black -> it.name.startsWith(black) }
            }
            testNodeDelay(parseProxies)
            isLoading.set(false)
        } else {
            log.d("testNodeDelay result already")
            nodeResultLiveData.postValue(NodeStorageResult(null))
        }
    }.getOrElse {
        isLoading.set(false)
        log.d("testNodeDelay result catch")
        nodeResultLiveData.postValue(NodeStorageResult(null))
    }

    suspend fun testNodeDelay(parseProxies: List<ProxyNodeState>) {
        NodeDelay.test(parseProxies) {
            log.d("testNodeDelay result callback")
            NodeConfig.saveNodeList(gson.toJson(it.list))
            nodeResultLiveData.postValue(NodeStorageResult(it.list, it.isFirst))
//            isLoading.set(!it.finish)
        }
    }

    fun getCacheNode() {
        NodeSubscribe.featCache()
        val list = runCatching {
            val json = NodeConfig.nodeListJson()
            if (json.isNotEmpty()) {
                val type = object : TypeToken<List<ProxyNodeState>>() {}.type
                gson.fromJson<List<ProxyNodeState>>(json, type)
            } else listOf()
        }.getOrElse { listOf() }
        nodeResultLiveData.postValue(NodeStorageResult(list))
    }
}