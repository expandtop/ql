package com.proxy.base.func

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import ex.ss.lib.tools.extension.formatDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentLinkedDeque

/**
 * 2024/10/9
 */
object NetworkLog : CoroutineScope by MainScope() {

    private lateinit var logFile: File

    private val logs = ConcurrentLinkedDeque<String>()

    fun init(context: Context) {
        logFile = File(context.cacheDir, "net.log")
        if (isMainProcess(context)) {
            if (logFile.exists()) logFile.delete()
            append(System.currentTimeMillis().formatDate())
        }
        launch {
            while (true) {
                val item = logs.poll()
                if (item != null) {
                    writeFile(logFile, item)
                } else {
                    delay(100)
                }
            }
        }
    }

    private suspend fun writeFile(file: File, content: String) = withContext(Dispatchers.IO) {
        file.appendText("$content\n", Charsets.UTF_8)
    }

    fun append(log: String) {
        logs.offer(log)
    }

    suspend fun getLogs(): String = withContext(Dispatchers.IO) {
        return@withContext logFile.readText()
    }

    private fun isMainProcess(context: Context): Boolean {
        return runCatching {
            val pid = Process.myPid()
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val processName = manager.runningAppProcesses.firstOrNull { it.pid == pid }?.processName
            processName == context.packageName
        }.getOrElse { false }
    }

}