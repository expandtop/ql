package com.proxy.base.config

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proxy.base.proxy.ProxyMode
import com.proxy.base.proxy.ProxyNodeState
import ex.ss.lib.components.log.SSLog
import ex.ss.lib.components.mmkv.IMMKVDelegate
import ex.ss.lib.components.mmkv.kvDelegate
import ex.ss.lib.tools.extension.ifNullOrEmpty

object NodeConfig : IMMKVDelegate() {

    private val log by lazy { SSLog.create("NodeConfig") }

    private val gson by lazy { Gson() }

    private var selectNodeUrl: String by kvDelegate("")
    private var selectNodeType: String by kvDelegate("")
    private var proxyMode: String by kvDelegate("")
    private var proxyModeName: String by kvDelegate("")
    private var proxiesNode: String by kvDelegate("")
    private var proxiesDelay: String by kvDelegate("")

    var lastStartTime: Long by kvDelegate(0)
    var isFirstTest: Boolean by kvDelegate(true)

    fun selectedProxyNode(): ProxyNodeState? {
        return runCatching {
            return ProxyNodeState.create(selectNodeUrl, selectNodeType)
        }.getOrElse {
            null
        }
    }

    fun selectedProxyNodeType(): String {
        return selectNodeType
    }

    fun saveSelectedProxyNode(node: ProxyNodeState?) {
        selectNodeUrl = node?.url.ifNullOrEmpty { "" }
        selectNodeType = node?.type.ifNullOrEmpty { "" }
    }

    fun proxyMode(): String {
        return proxyMode.ifNullOrEmpty { ProxyMode.Rule.value }
    }

    fun saveProxyMode(mode: String) {
        proxyMode = mode
    }

    fun proxyModeName(): String {
        return proxyModeName.ifNullOrEmpty { "智能模式" }
    }

    fun saveProxyModeName(name: String) {
        proxyModeName = name
    }

    fun nodeListJson(): String {
        return proxiesNode
    }

    fun saveNodeList(nodeListJson: String) {
        proxiesNode = nodeListJson
    }

    fun nodeDelay(): Map<String, Int> {
        if (proxiesDelay.isEmpty()) return mapOf()
        val type = object : TypeToken<Map<String, Int>>() {}.type
        return gson.fromJson(proxiesDelay, type)
    }

    fun saveNodeDelay(delay: Map<String, Int>) {
        if (delay.isEmpty()) return
        proxiesDelay = gson.toJson(delay)
    }

    override fun mmkvName(): String = "NodeConfig"

    fun isGroupExpand(name: String): Boolean {
        return mmkv().decodeBool(name)
    }

    fun setGroupExpand(name: String, expand: Boolean) {
        mmkv().encode(name, expand)
    }

}