package com.proxy.base.proxy

import android.util.Log
import com.google.gson.Gson
import com.jfdream.xvpn.vpn.VPNParser
import com.proxy.base.util.proxy.ProxyTest
import ex.ss.lib.base.adapter.data.BaseItem
import ex.ss.lib.base.adapter.data.BaseMultiItem
import java.net.URLDecoder

/**
 * 2024/9/11
 */


data class ProxyNodeStateGroup(
    val name: String,
    val isExpand: Boolean
) : BaseMultiItem

data class ProxyNodeState(
    val url: String,
    val type: String = "",
    val name: String,
    val address: String,
    val protocol: String,
    val port: Int,

    val isSelect: Boolean = false,
    val delay: Int = ProxyTest.TIME_OUT_DELAY,
    val rate: Float = 0F,
) : BaseMultiItem {

    fun same(node: ProxyNodeState?): Boolean {
        return node != null && url == node.url && type == node.type
    }

    companion object {
        fun create(url: String, type: String = ""): ProxyNodeState {
            check(url.isNotEmpty()) { "IProxyNode: url must be not empty" }
            val parse = VPNParser.parse(url)
            val name =
                parse.getOrDefault("remark", "").toString().let { URLDecoder.decode(it, "UTF-8") }
            val address = parse.getOrDefault("address", "").toString()
            val port = parse.getOrDefault("port", 0).toString().toInt()
            val protocol = parse["xx"]?.toString() ?: ""
            return ProxyNodeState(url, type, name, address, protocol, port)
        }
    }
}