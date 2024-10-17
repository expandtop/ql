package com.proxy.base.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


sealed class Page
object EmptyPage : Page()
object MainPage : Page() {
    object PayPage : Page()
}

object PageRouter : CoroutineScope by MainScope() {
    private val mainPageFlow: MutableStateFlow<Page> = MutableStateFlow(EmptyPage)
    fun observePage(owner: LifecycleOwner, block: (Page) -> Unit) {
        owner.lifecycleScope.launch {
            mainPageFlow.flowWithLifecycle(owner.lifecycle, Lifecycle.State.RESUMED).collectLatest {
                if (it != EmptyPage) {
                    withContext(Dispatchers.Main) { block.invoke(it) }
                }
            }
        }
    }

    fun showPayPage() = launch {
        mainPageFlow.emit(MainPage.PayPage)
    }

    fun showPageEnd() = launch {
        mainPageFlow.emit(EmptyPage)
    }

}