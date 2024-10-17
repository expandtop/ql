package com.proxy.base.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

object ProxyTimer : CoroutineScope by MainScope() {

    private var timerJob: Job? = null

    private var listener: ((Long) -> Unit)? = null

    private val isRunning = AtomicBoolean(false)
    private val initializeTime = AtomicLong(0)

    fun onListener(owner: LifecycleOwner, listener: (Long) -> Unit) {
        ProxyTimer.listener = listener
        owner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                ProxyTimer.listener = null
            }
        })
    }

    fun start(initTime: Long? = null) {
        if (isRunning.get()) return
        initializeTime.set(initTime ?: System.currentTimeMillis())
        startTimer()
    }

    fun stop() {
        if (!isRunning.get()) return
        initializeTime.set(0)
        stopTimer()
        listener?.invoke(0L)
    }

    private fun startTimer() {
        if (initializeTime.get() <= 0) return
        timerJob = launch {
            isRunning.set(true)
            while (true) {
                if (initializeTime.get() <= 0) {
                    withContext(Dispatchers.Main) { listener?.invoke(0L) }
                    break
                } else {
                    withContext(Dispatchers.Main) {
                        val time = System.currentTimeMillis() - initializeTime.get()
                        listener?.invoke(time)
                        delay(1000)
                    }
                }
            }
            isRunning.set(false)
        }
    }

    private fun stopTimer() {
        isRunning.set(false)
        timerJob?.cancel()
    }


}