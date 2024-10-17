package com.proxy.base.ui.dialog

import android.graphics.Color
import android.os.Build
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding
import ex.ss.lib.base.dialog.BaseDialog

abstract class BasicDialog<T : ViewBinding> : BaseDialog<T>() {

    override fun dimAmount(): Float = 0.7F
    override fun isFullWidth(): Boolean = true
    override fun isFullHeight(): Boolean = true

    override fun onStart() {
        super.onStart()
        controlSystemUI()
    }

    private fun controlSystemUI() {
        val window = dialog?.window ?: return
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
    }
}