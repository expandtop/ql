package com.proxy.base.config

import com.google.gson.Gson
import com.proxy.base.data.CommChannelConfig
import com.proxy.base.data.CommConfig
import ex.ss.lib.components.mmkv.IMMKVDelegate
import ex.ss.lib.components.mmkv.kvDelegate

object AppConfig : IMMKVDelegate() {
    private val gson by lazy { Gson() }
    override fun mmkvName(): String = "appConfig"

    var isFirstOpen by kvDelegate(true)

    var deviceId by kvDelegate("")
    var channel by kvDelegate("")

    var dynamicDomain by kvDelegate("")

    private var commConfig by kvDelegate("")
    private var commChannelConfig by kvDelegate("")

    fun saveCommConfig(data: CommConfig) {
        commConfig = gson.toJson(data)
    }

    fun saveCommChannelConfig(data: CommChannelConfig) {
        commChannelConfig = gson.toJson(data)
    }

    fun getCommConfig(): CommConfig? {
        return runCatching {
            gson.fromJson(commConfig, CommConfig::class.java)
        }.getOrNull()
    }

    fun getCommChannelConfig(): CommChannelConfig? {
        return runCatching {
            gson.fromJson(commChannelConfig, CommChannelConfig::class.java)
        }.getOrNull()
    }

}