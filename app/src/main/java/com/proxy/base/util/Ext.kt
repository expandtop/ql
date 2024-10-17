package com.proxy.base.util

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun ComponentActivity.addBackCallback(enable: Boolean = true, callBack: () -> Unit) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(enable) {
        override fun handleOnBackPressed() {
            callBack.invoke()
        }
    })
}

fun <T> Flow<T>.collectOwner(owner: LifecycleOwner, action: suspend T.() -> Unit = {}) =
    owner.lifecycleScope.launch {
        collectLatest(action)
    }

fun <T> Flow<T?>.collectOwnerNotNull(
    owner: LifecycleOwner,
    action: suspend (value: T) -> Unit = {},
) = owner.lifecycleScope.launch {
    collectLatest { data ->
        data?.also {
            action.invoke(it)
        }
    }
}

fun <T> Flow<T>.collectThis(activity: FragmentActivity) {
    collectOwner(activity)
}

fun <T> Flow<T>.collectThis(fragment: Fragment) {
    collectOwner(fragment.viewLifecycleOwner)
}

fun <T> T.toBundle(): Bundle {
    val json = Gson().toJson(this)
    return bundleOf("AnyToGsonBundle" to json)
}

fun <T> Class<T>.fromBundle(bundle: Bundle?): T {
    val json = (bundle ?: bundleOf()).getString("AnyToGsonBundle")
    return Gson().fromJson(json, this)
}


fun Int.secondExpire(): Boolean {
    return System.currentTimeMillis() / 1000 > this
}

fun Long.expire(): Boolean {
    return System.currentTimeMillis() > this
}
