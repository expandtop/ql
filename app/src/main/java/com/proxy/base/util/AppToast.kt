package com.proxy.base.util

import android.app.Application
import androidx.fragment.app.FragmentActivity
import com.proxy.base.func.ActivityLifecycleExt
import com.proxy.base.ui.dialog.showToastDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 2024/7/18
 */
object AppToast : CoroutineScope by MainScope() {

    private val lifecycleExt by lazy { ActivityLifecycleExt() }

    fun initialize(application: Application) {
        lifecycleExt.initialize(application)
    }

    fun show(msg: String) = launch(Dispatchers.Main) {
        if (isBlack(msg)) return@launch
        val activity = lifecycleExt.getActivity()
        if (msg.isNotEmpty() && activity != null) {
            (activity as? FragmentActivity)?.showToastDialog(msg)
//            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun isBlack(msg: String): Boolean {
        for (item in BLACK_MSG_LIST) {
            if (msg.lowercase().startsWith(item.lowercase())) {
                return true
            }
        }
        return false
    }

    private val BLACK_MSG_LIST = hashSetOf("Software caused connection abort", "job")

}