package com.proxy.base.func

import android.app.Application
import android.content.Context
import com.proxy.base.BuildConfig
import com.proxy.base.config.AppConfig
import com.umeng.commonsdk.UMConfigure
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * 2024/9/24
 */
object Umeng {

    fun initialize(application: Application) {
        UMConfigure.setLogEnabled(BuildConfig.DEBUG)
        UMConfigure.preInit(application, "6661394b940d5a4c496631bb", AppConfig.channel)
        UMConfigure.submitPolicyGrantResult(application, true)
        UMConfigure.init(
            application,
            "6661394b940d5a4c496631bb",
            AppConfig.channel,
            UMConfigure.DEVICE_TYPE_PHONE,
            ""
        )
    }

    suspend fun syncGetOaid(context: Context): String = suspendCancellableCoroutine { c ->
        UMConfigure.getOaid(context) { id ->
            if (!id.isNullOrEmpty() && !id.all { it == '0' }) {
                c.resume(id)
            } else {
                c.resume("")
            }
        }
    }

    fun asyncGetOaid(context: Context, onCallback: (String) -> Unit) {
        UMConfigure.getOaid(context) { id ->
            if (!id.isNullOrEmpty() && !id.all { it == '0' }) {
                onCallback.invoke(id)
            } else {
                onCallback.invoke("")
            }
        }
    }

}