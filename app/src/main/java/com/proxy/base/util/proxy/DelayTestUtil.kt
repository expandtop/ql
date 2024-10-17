package com.proxy.base.util.proxy

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.UnknownHostException
import java.util.regex.Pattern
import kotlin.math.min


object DelayTestUtil {

    private val tcpTestingSockets = ArrayList<Socket?>()

    suspend fun tcping(url: String, port: Int, timeout: Int): Int = withContext(Dispatchers.IO) {
        var time = -1
        for (k in 0 until 1) {
            val one = socketConnectTime(url, port, timeout)
            if (!coroutineContext.isActive) {
                break
            }
            if (one != -1 && (time == -1 || one < time)) {
                time = one
            }
        }
        return@withContext time
    }

    private fun socketConnectTime(url: String, port: Int, timeout: Int): Int {
        try {
            val socket = Socket()
            synchronized(this) {
                tcpTestingSockets.add(socket)
            }
            val start = System.currentTimeMillis()
            socket.connect(InetSocketAddress(url, port), timeout)
            val time = System.currentTimeMillis() - start
            synchronized(this) {
                tcpTestingSockets.remove(socket)
            }
            socket.close()
            return minOf(timeout, time.toInt())
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }

    fun closeAllTcpSockets() {
        synchronized(this) {
            tcpTestingSockets.forEach {
                it?.close()
            }
            tcpTestingSockets.clear()
        }
    }

    suspend fun icmp(url: String, port: Int, timeout: Int): Int = withContext(Dispatchers.IO) {
        var time = -1
        for (k in 0 until 1) {
            val one = icmpTime(url, port, timeout)
            if (one != -1 && (time == -1 || one < time)) {
                time = one
            }
        }
        return@withContext time
    }

    private suspend fun icmpTime(url: String, port: Int, timeout: Int): Int {
        return withContext(Dispatchers.IO) {
            runCatching {
                val start = System.currentTimeMillis()/* 当返回值是true时，说明host是可用的，false则不可。 */
                val reachable = InetAddress.getByName(url).isReachable(timeout)
                val time = System.currentTimeMillis() - start
                if (reachable) {
                    minOf(timeout, time.toInt())
                } else -1
            }.getOrElse {
                -1
            }
        }
    }

    suspend fun ping(url: String, port: Int, timeout: Int): Int = withContext(Dispatchers.IO) {
        var time = -1
        for (k in 0 until 1) {
            val one = pingTime(url, port, timeout)
            if (one != -1 && (time == -1 || one < time)) {
                time = one
            }
        }
        return@withContext time
    }

    private suspend fun pingTime(url: String, port: Int, timeout: Int): Int {
        return runCatching {
            val process = Runtime.getRuntime().exec("ping -c 3 -w $timeout $url")
            val exit = waitExitValue(process, timeout * 1L)
            println("Delay:exit = $exit")
            if (exit == 0) {
                val result = process.inputStream.reader().readLines().onEach {
                    println("Delay:$it")
                }
                PintParse.parseGetAverage(result)
            } else {
                -1
            }
        }.getOrElse {
            -1
        }
    }


    private suspend fun waitExitValue(process: Process, timeout: Long): Int {
        var all = 0L
        val rem = 100L
        do {
            try {
                return process.exitValue()
            } catch (ex: Throwable) {
                all += rem
                if (all >= timeout) break
                delay(min(rem, 100L))
            }
        } while (true)
        return -1
    }


}

/**
 * PING:qqa813.qingqiu.buzz ---------------------
 * PING:PING qqa813.qingqiu.buzz (36.138.176.204) 56(84) bytes of data.
 * PING:64 bytes from 36.138.176.204: icmp_seq=1 ttl=41 time=30.3 ms
 * PING:64 bytes from 36.138.176.204: icmp_seq=2 ttl=41 time=35.7 ms
 * PING:64 bytes from 36.138.176.204: icmp_seq=3 ttl=41 time=45.7 ms
 * PING:
 * PING:--- qqa813.qingqiu.buzz ping statistics ---
 * PING:3 packets transmitted, 3 received, 0% packet loss, time 2003ms
 * PING:rtt min/avg/max/mdev = 30.397/37.309/45.798/6.387 ms
 */
object PintParse {
    private val responsePattern by lazy { Pattern.compile("""bytes from (?<ip>.*): icmp_seq=(?<seq>\d+) ttl=(?<ttl>\d+) time=(?<time>(\d+).?(\d+))""") }
    private val timePattern by lazy { Pattern.compile("""time=(-?\d+)(\.\d+)?""") }

    fun parseGetAverage(line: List<String>): Int {
        val time = line.map { it.lowercase() }.filter {
            responsePattern.matcher(it).find()
        }.map {
            timePattern.matcher(it)
                .let { matcher -> if (matcher.find()) matcher.group(0) else null }
        }.map {
            it?.split("=")?.getOrNull(1)?.toFloat() ?: -1F
        }
        return time.average().toInt()
    }
}
