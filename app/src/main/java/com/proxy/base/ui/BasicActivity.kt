package com.proxy.base.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding
import com.proxy.base.R
import com.proxy.base.config.UserConfig
import com.proxy.base.proxy.VPNProxy
import com.proxy.base.repo.Remote
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.login.LoginActivity
import ex.ss.lib.base.activity.BaseActivity

abstract class BasicActivity<VB : ViewBinding> : BaseActivity<VB>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Remote.loginExpireLiveData.observe(this) {
            if (it) {
                Remote.resetLoginExpire()
                loginExpire()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) return
        controlSystemUI()
    }

    override fun onStart() {
        super.onStart()
        controlSystemUI()
    }

    private fun controlSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controllerCompat = WindowCompat.getInsetsController(window, window.decorView)
        controllerCompat.isAppearanceLightStatusBars = false
        controllerCompat.isAppearanceLightNavigationBars = true

        //异形屏
        val attributes = window.attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        window.attributes = attributes

        //透明状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = getColor(R.color.main_bg_color)
    }

    protected fun <T : Activity> startActivity(clazz: Class<T>, params: Intent.() -> Unit = {}) {
        val intent = Intent(this, clazz)
        params.invoke(intent)
        startActivity(intent)
    }


    //登录过期
    protected fun loginExpire() {
        dismissLoading()
        if (this@BasicActivity is LoginActivity) return
        VPNProxy.stopTunnel()
        UserConfig.logout()
        startActivity(LoginActivity::class.java) {
            setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

}