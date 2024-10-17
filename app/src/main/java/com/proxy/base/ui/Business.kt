package com.proxy.base.ui

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.proxy.base.BuildConfig
import com.proxy.base.config.UserConfig
import com.proxy.base.ui.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 2024/7/29
 */

suspend fun Fragment.checkLoginSuspend(block: suspend () -> Unit): Boolean {
    val check = withContext(Dispatchers.Main) { checkLogin() }
    if (check) {
        block.invoke()
        return true
    }
    return false
}

fun Fragment.checkLogin(block: () -> Unit): Boolean {
    if (checkLogin()) {
        block.invoke()
        return true
    }
    return false
}

fun Fragment.checkLogin(): Boolean {
    if (UserConfig.isLogin()) {
        return true
    } else {
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        return false
    }
}

fun FragmentActivity.checkLogin(block: () -> Unit): Boolean {
    if (checkLogin()) {
        block.invoke()
        return true
    }
    return false
}

fun FragmentActivity.checkLogin(): Boolean {
    if (UserConfig.isLogin()) {
        return true
    } else {
        startActivity(Intent(this, LoginActivity::class.java))
        return false
    }
}
