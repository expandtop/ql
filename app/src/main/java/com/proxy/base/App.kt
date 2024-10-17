package com.proxy.base

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.proxy.base.config.AppConfig
import com.proxy.base.config.UserConfig
import com.proxy.base.func.NetworkLog
import com.proxy.base.func.Umeng
import com.proxy.base.model.AppViewModel
import com.proxy.base.proxy.VPNProxy
import com.proxy.base.repo.DomainChangeInterceptor
import com.proxy.base.repo.Remote
import com.proxy.base.util.AppToast
import ex.ss.lib.base.SSBase
import ex.ss.lib.components.SSComponents
import ex.ss.lib.components.log.SSLog
import ex.ss.lib.components.startup.StartupBuilder
import ex.ss.lib.components.startup.StartupComponent
import ex.ss.lib.components.startup.syncStartup
import ex.ss.lib.net.SSNet
import ex.ss.lib.net.interceptor.CommonHeaderInterceptor
import ex.ss.lib.tools.common.APKTools
import kotlinx.coroutines.launch
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        StartupComponent.onCreate(this) {
            log()
            base()
            appInit()
            net()
            vpn()
        }
    }
}

fun StartupBuilder.log() = syncStartup {
    NetworkLog.init(it)
    SSLog.debug(BuildConfig.DEBUG)
//    SSLog.println { priority, tag, msg ->
//        Log.println(priority, tag, msg)
//    }
}

private fun StartupBuilder.base() = syncStartup {
    SSBase.initialize(it)
    SSBase.setImmersiveBar(true)
    SSBase.setImmersiveBarColor(it.getColor(R.color.main_bg_color))
//    StatusBarUtils.setLightMode(invokeApplication(), true)

    SSComponents.initialize(it)
    SSComponents.initMMKV {
        val crypt = APKTools.getSignNature(it).slice(0 until 16)
        setDefCryptKey(crypt)
        setRootPath(File(it.filesDir, "app").absolutePath)
        File(it.filesDir, "mmkv").delete()
    }
}

fun StartupBuilder.appInit() = syncStartup {
    if (AppConfig.channel.isEmpty()) {
        AppConfig.channel = "android"
    }
    Umeng.initialize(invokeApplication())
    ProcessLifecycleOwner.get().apply {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                AppViewModel.onProcessResume()
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                AppViewModel.onProcessStop()
            }
        }
    }

    AppToast.initialize(invokeApplication())
}


fun StartupBuilder.net() = syncStartup {
    val netLog by lazy { SSLog.create("RemoteHttp") }
    Remote.initErrorMessage(it)
    SSNet.initialize(BuildConfig.BASE_URL) {
        trustAllManager = true
        timeoutSeconds = 60
        okhttpBuilder = {
            addNetworkInterceptor(
                HttpLoggingInterceptor {
                    netLog.d(it.toByteArray(Charsets.US_ASCII).toString(Charsets.UTF_8))
                }.setLevel(
                    HttpLoggingInterceptor.Level.BODY
                )
            )
            addInterceptor(DomainChangeInterceptor())
            addInterceptor(CommonHeaderInterceptor {
                mutableMapOf(
                    "authorization" to UserConfig.auth_data,
                    "channel" to AppConfig.channel,
                    "model" to "${Build.BRAND} ${Build.MODEL}",
                )
            })
        }
    }
}

fun StartupBuilder.vpn() = syncStartup {
    VPNProxy.initialize(it, BuildConfig.DEBUG)
}