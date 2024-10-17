package com.proxy.base.ui.splash

import android.content.Context
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.request.CachePolicy
import com.proxy.base.config.AppConfig
import com.proxy.base.config.UserConfig
import com.proxy.base.data.UnitAd
import com.proxy.base.databinding.ActivitySplashBinding
import com.proxy.base.func.Umeng
import com.proxy.base.model.AppViewModel
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.ui.login.LoginActivity
import com.proxy.base.ui.main.MainActivity
import ex.ss.lib.base.extension.viewBinding
import ex.ss.lib.components.log.SSLog
import ex.ss.lib.tools.common.DevicesTools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SplashActivity : BasicActivity<ActivitySplashBinding>() {

    override val binding: ActivitySplashBinding by viewBinding()
    private val log by lazy { SSLog.create("Splash") }

    private val gotoMain = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.ivAdClose.setOnClickListener {
            goto()
        }
        initialize()
    }

    private fun initialize() = lifecycleScope.launch {
        withContext(Dispatchers.Main) { showLoading() }
        getDevicesId(this@SplashActivity)
        AppViewModel.syncGetCommConfig()
        AppViewModel.syncGetCommChannelConfig()
        val syncResult = syncUser()
        gotoMain.set(syncResult)
        val unitAd = syncAd()
        withContext(Dispatchers.Main) { dismissLoading() }
        if (unitAd != null) showAd(unitAd)
        goto()
    }

    private suspend fun syncAd(): UnitAd? {
        val splashAd = AppViewModel.syncSplashAd()
        if (splashAd.isSuccess()) {
            return splashAd.data?.firstOrNull().takeIf { it?.isImageAd() == true }
        }
        return null
    }

    /**
     * 跳转首页
     */
    private suspend fun syncUser(): Boolean {
        if (UserConfig.isLogin()) {
            UserViewModel.syncUserInfo()
            return true
        } else {
            if (!AppConfig.isFirstOpen) return false
            return retryDeviceLogin()
        }
    }

    private suspend fun retryDeviceLogin(): Boolean {
        for (i in 0 until 3) {
            val data = UserViewModel.syncDeviceLogin()
            if (data.isSuccess()) return true
            delay(1000)
        }
        return false
    }

    private suspend fun getDevicesId(context: Context) = withContext(Dispatchers.IO) {
        if (AppConfig.deviceId.isEmpty()) {
            val oaid = Umeng.syncGetOaid(context)
            log.d("oaid:$oaid")
            if (oaid.isNotEmpty()) {
                AppConfig.deviceId = oaid
            } else {
                AppConfig.deviceId = DevicesTools.androidId(context)
            }
            log.d("deviceId:${AppConfig.deviceId}")
        }
    }

    private suspend fun showAd(ad: UnitAd) = suspendCoroutine { co ->
        binding.ivAd.isVisible = true
        binding.ivAd.load(ad.cover) {
            diskCachePolicy(CachePolicy.ENABLED)
            listener(onCancel = {
                co.resume(Unit)
            }, onError = { _, _ ->
                co.resume(Unit)
            }, onSuccess = { _, _ ->
                binding.ivAdClose.isVisible = true
                lifecycleScope.launch {
                    delay(((Math.random() * 2 + 3) * 1000L).toLong())
                    co.resume(Unit)
                }
            })
        }
    }

    private fun goto() {
        if (gotoMain.get()) {
            startActivity(MainActivity::class.java)
        } else {
            startActivity(LoginActivity::class.java)
        }
        finish()
    }

    override fun initData() {
    }

    override fun initView() {

    }
}