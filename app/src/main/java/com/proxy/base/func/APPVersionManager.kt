package com.proxy.base.func

import androidx.fragment.app.FragmentActivity
import com.proxy.base.model.AppViewModel
import com.proxy.base.ui.dialog.DialogManager
import com.proxy.base.ui.dialog.showNewVersionDialog
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.util.BrowserUtils
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.toast

/**
 * 2024/9/23
 */
object APPVersionManager {

    fun checkVersion(fragmentActivity: FragmentActivity,forceShow:Boolean = false) = kotlin.runCatching {
        AppViewModel.version().collectOwner(fragmentActivity) {
            onSuccess {
                if (it.hasNewVersion() && (forceShow || DialogManager.isShowNewVersion.compareAndSet(false, true))) {
                    fragmentActivity.showNewVersionDialog(it) {
                        BrowserUtils.openBrowser(fragmentActivity, it.link)
                    }
                } else {
                    if (forceShow) fragmentActivity.showToastDialog("已经是最新版本")
                }
            }
        }
    }

}