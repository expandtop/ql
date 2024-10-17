package com.proxy.base.proxy

import com.google.gson.Gson

data class VPNRouteConfig(val list: List<VPNRouteConfigItem>) {
    fun get(): String {
        return Gson().toJson(list)
    }
}

//"[{method:\"direct\",type:\"domain\",content:\"${host}\"}]"
data class VPNRouteConfigItem(val method: String, val type: String, val content: String)
