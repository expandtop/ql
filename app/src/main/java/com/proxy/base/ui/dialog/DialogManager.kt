package com.proxy.base.ui.dialog

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.proxy.base.R
import com.proxy.base.data.UnitAd
import com.proxy.base.data.VersionInfo
import com.proxy.base.model.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object DialogManager {
    val isShowNewVersion = AtomicBoolean(false)
}

fun FragmentActivity.showNewVersionDialog(it: VersionInfo, onDone: () -> Unit) {
    AppDialog.show(supportFragmentManager) {
        title = it.appname
        content = it.explain
        cancelOutside = it.force != 1
        alwaysShow = it.force == 1
        done = getString(R.string.new_version_done)
        cancel = if (it.force != 1) getString(R.string.new_version_cancel) else ""
        this.onDone = onDone
    }
}

fun FragmentActivity.showAd(ads: List<UnitAd>) = lifecycleScope.launch(Dispatchers.Main) {
    for (ad in ads) {
        if (ad.isImageAd()) {
            Log.d("MAIN_AD", "isImageAd")
            showImageAd(ad)
        } else if (ad.isTextAd()) {
            Log.d("MAIN_AD", "isTextAd")
            showTextAd(ad)
        } else {
            Log.d("MAIN_AD", "Empty Ad")
        }
    }
}

private suspend fun FragmentActivity.showImageAd(ad: UnitAd): Boolean = suspendCoroutine { co ->
    Log.d("MAIN_AD","showImageAd")
    if (ad.isImageAd()) {
        AppViewModel.requireCommConfig {
            val url = it.adimageurl + ad.cover
            Log.d("MAIN_AD", "show ImageAd $url")
            AdImageDialog.show(supportFragmentManager, url, ad.link).setOnDismissCallback {
                co.resume(true)
            }
        }
    } else {
        co.resume(false)
    }
}

private suspend fun FragmentActivity.showTextAd(ad: UnitAd) = suspendCancellableCoroutine { co ->
    if (ad.isTextAd()) {
        Log.d("MAIN_AD", "show TextAd")
        AdTextDialog.show(supportFragmentManager, ad.tagline ?: "", ad.link).setOnDismissCallback {
            co.resume(true)
        }
    } else {
        co.resume(false)
    }
}
