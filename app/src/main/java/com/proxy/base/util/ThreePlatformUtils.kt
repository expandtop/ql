package com.proxy.base.util

import android.content.Context
import android.content.Intent
import android.net.Uri


object ThreePlatformUtils {

    private  val TG_PKG_LIST = listOf(
        "org.telegram.messenger", "org.telegram.messenger.web", "org.thunderdog.challegram"
    )

    fun openTG(context: Context, link: String, openBrowser: Boolean = true): Boolean {
        return runCatching {
            val pkg = TG_PKG_LIST.firstOrNull { checkAppInstalled(context, it) }
            if (pkg.isNullOrEmpty()) {
                if (openBrowser) {
                    openBrowser(context, link)
                    true
                } else {
                    false
                }
            } else {
                Intent(Intent.ACTION_VIEW, Uri.parse(link)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    setPackage(pkg)
                    context.startActivity(this)
                }
                true
            }

        }.getOrElse { false }
    }

    fun checkAppInstalled(context: Context, packageName: String): Boolean {
        return runCatching {
            context.packageManager.getPackageInfo(packageName, 0) != null
        }.getOrElse { false }
    }

    /*打开浏览器*/
    fun openBrowser(context: Context, url: String, title: String = ""): Boolean {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (checkAppInstalled(context, "com.android.browser")) {
            return runCatching {
                intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity")
                context.startActivity(intent)
                true
            }.getOrElse { false }
        } else {
            return runCatching {
                context.startActivity(intent)
                true
            }.getOrElse {
                runCatching {
                    context.startActivity(Intent.createChooser(intent, title))
                    true
                }.getOrElse { false }
            }
        }
    }

}