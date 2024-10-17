package com.proxy.base.model

import com.proxy.base.config.AppConfig
import com.proxy.base.config.UserConfig
import com.proxy.base.data.CommChannelConfig
import com.proxy.base.data.CommConfig
import com.proxy.base.repo.Remote
import ex.ss.lib.components.log.SSLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object AppViewModel : BaseViewModel() {

    private val log by lazy { SSLog.create("AppViewModel") }

    suspend fun syncGetCommConfig() = Remote.callBase { commConfig() }.onSuccess {
        AppConfig.saveCommConfig(it)
    }

    fun getCommConfig() = apiBase { commConfig() }.onEach {
        if (it.isSuccess()) {
            AppConfig.saveCommConfig(it.data)
        }
    }

    fun requireCommConfig(block: (CommConfig) -> Unit = {}) {
        val commConfig = AppConfig.getCommConfig()
        if (commConfig != null) {
            commConfig.also(block)
        } else {
            launch(Dispatchers.IO) {
                getCommConfig().collect {
                    if (it.isSuccess()) {
                        AppConfig.saveCommConfig(it.data)
                        withContext(Dispatchers.Main) { it.data.also(block) }
                    }
                }
            }
        }
    }

    suspend fun syncGetCommChannelConfig() = Remote.callBase { commChannelConfig() }.onSuccess {
        AppConfig.saveCommChannelConfig(it)
    }


    fun getCommChannelConfig() = apiBase { commChannelConfig() }.onEach {
        if (it.isSuccess()) {
            AppConfig.saveCommChannelConfig(it.data)
        }
    }

    fun requireCommChannelConfig(block: (CommChannelConfig) -> Unit = {}) {
        val commChannelConfig = AppConfig.getCommChannelConfig()
        if (commChannelConfig != null) {
            commChannelConfig.also(block)
        } else {
            launch(Dispatchers.IO) {
                getCommChannelConfig().collect {
                    if (it.isSuccess()) {
                        AppConfig.saveCommChannelConfig(it.data)
                        withContext(Dispatchers.Main) { it.data.also(block) }
                    }
                }
            }
        }
    }

    fun getNotice() = api { notice() }

    fun getHelp() = apiBase { help("android") }

    fun navCate() = apiBase { navCate() }

    fun navIndex(id: Int) = api { navIndex(id) }

    fun version() = apiBase { version("android", AppConfig.channel) }

    fun aboutusTos() = apiBase { aboutusTos("tos") }

    fun splashAd() = apiBase { splashAd("1", "android", AppConfig.channel) }

    suspend fun syncSplashAd() = Remote.callBase { splashAd("1", "android", AppConfig.channel) }

    fun mainAd() = apiBase { mainAd("2", "android", AppConfig.channel) }

    fun onProcessResume() {
        log.d("进入前台")
        if (UserConfig.isLogin()) {
            log.d("进入前台，刷新一次用户信息")
            UserViewModel.refreshUserInfo()
            if (UserConfig.paying) {
                log.d("触发了支付，刷新一次节点")
                UserConfig.paying = false
                NodeViewModel.featSubscribe()
                launch(Dispatchers.IO) {
                    repeat(3) {
                        delay(1000L * (it + 1))
                        NodeViewModel.featOnlySubInfo()
                    }
                }
            }
        }
    }

    fun onProcessStop() {

    }


}