package com.proxy.base.repo.dynamic

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.proxy.base.BuildConfig
import ex.ss.lib.components.log.SSLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

object DynamicMultiDomainConfig : CoroutineScope by MainScope() {

    private val log by lazy { SSLog.create("DynamicDomainManager") }

    private val requestLock = Any()
    private val currentConfig = AtomicReference<List<DynamicConfig>>(null)

    fun featConfigWrapper(): List<DynamicConfig> = synchronized(requestLock) {
        val list = featConfig().map {
            if (!it.new.startsWith("http") && !it.isIP) {
                val randomPath = getRandomSubPath()
                it.copy(new = "https://$randomPath.${it.new}/api/v1")
            } else {
                it.copy(new = "${it.new}/api/v1")
            }
        }
        return@synchronized list.onEach {
            log.d("featConfigWrapper : ${it.new}")
        }
    }

    private fun getRandomSubPath(): String {
        val length = Math.random() * 10 + 3
        val charset = "abcdefghijklmnopqrstuvwxyz"
        return (1..length.toInt()).map { charset.random() }.joinToString("")
    }

    private fun featConfig(): List<DynamicConfig> = synchronized(requestLock) {
        val config = currentConfig.get()
        if (config != null) {
            log.d("featConfig already config")
            return@synchronized config
        }
        log.d("featConfig request config")
        return DynamicConfigRequest.loadConfig().apply {
            currentConfig.set(this)
        }
    }


}

internal object DynamicConfigRequest {

    private val log by lazy { SSLog.create("DynamicConfigRequest") }

    private val gson by lazy { Gson() }

    private val okHttpClient by lazy {
        OkHttpClient.Builder().writeTimeout(10L, TimeUnit.SECONDS)
            .apply { TrustManager.trustAllHttpsCertificates(this) }
            .readTimeout(10L, TimeUnit.SECONDS).callTimeout(10L, TimeUnit.SECONDS)
            .connectTimeout(10L, TimeUnit.SECONDS).build()
    }

    fun loadConfig(): List<DynamicConfig> {
        val configList = mutableListOf<DynamicConfig>()
        configList.addAll(preInnerConfig())
        val remoteConfigList = originRemoteConfigAPI().map {
            requestDynamicConfig(listOf(it), 0, true)
        }.mapNotNull {
            runCatching { gson.fromJson(it, DynamicResult::class.java) }.getOrNull()
        }.flatMap {
            it.Answer.flatMap { it.data.split(",") }.map {
                DynamicConfig(true, it.replace("\"", ""))
            }
        }
        configList.addAll(remoteConfigList)
        val ossConfigList = originOssConfigAPI().map {
            requestDynamicConfig(listOf(it), 0, true)
        }.flatMap {
            it.split(",")
        }.map {
            DynamicConfig(true, it.replace("\"", ""))
        }
        configList.addAll(ossConfigList)
        configList.add(DynamicConfig(true, "http://159.75.228.117", isIP = true))
        val priorityIndex = AtomicInteger(0)
        val conflictSet = mutableSetOf<String>()
        return configList.map { it.copy(priority = priorityIndex.getAndIncrement()) }.filter {
            if (conflictSet.contains(it.new)) {
                false
            } else {
                conflictSet.add(it.new)
                true
            }
        }.apply {
            onEach {
                log.d("ITEM:${it.new} ${it.priority}")
            }
        }
    }

    private fun preInnerConfig(): List<DynamicConfig> {
        return listOf(
            DynamicConfig(true, BuildConfig.BASE_URL),
            DynamicConfig(true, "jzvvrvkk.cc"),
            DynamicConfig(true, "j2zj4ueb.cc"),
            DynamicConfig(true, "yvq2ap9p.cc"),
        )
    }

    private fun requestDynamicConfig(
        origin: List<String>, index: Int, useCache: Boolean
    ): String {
        runCatching {
            log.d("getDynamicConfig :${index}")
            val url = origin.getOrNull(index)
            if (url.isNullOrEmpty()) {
                return ""
            }
            log.d("getDynamicConfig :${url}")
            val request =
                Request.Builder().url(url).get().header("Accept", "application/dns-json").build()
            val response = okHttpClient.newCall(request).execute()
            if (response.code == 200) {
                return response.body?.string() ?: ""
            }
            throw IOException("")
        }.getOrElse {
            log.d("getDynamicConfig $it")
            if (index >= origin.size) return ""
            log.d("getDynamicConfig next")
            return requestDynamicConfig(origin, index + 1, useCache)
        }
    }

    private fun originRemoteConfigAPI(): List<String> {
        return listOf(
            "https://doh.pub/dns-query?name=hodlq.cc&type=16",
            "https://dns.alidns.com/resolve?name=hodlq.cc&type=16"
        )
    }

    private fun originOssConfigAPI(): List<String> {
        return listOf(
            "https://npv1-1324093666.cos.ap-shanghai.myqcloud.com/config.txt",
            "https://npv1.oss-cn-hongkong.aliyuncs.com/config.txt"
        )
    }
}

data class DynamicResult(val Answer: List<DynamicResultItem>)
data class DynamicResultItem(val data: String)

object TrustManager {

    /*信任所有HTTPS的证书*/
    fun trustAllHttpsCertificates(builder: OkHttpClient.Builder) {
        builder.hostnameVerifier { _, _ -> true }
        val sslContext = SSLContext.getInstance("TLS")
        val trustAllManager = trustAllManager()
        val trustAllManagers = arrayOf(trustAllManager())
        sslContext.init(null, trustAllManagers, SecureRandom())
        builder.sslSocketFactory(sslContext.socketFactory, trustAllManager)
    }

    @SuppressLint("CustomX509TrustManager")
    private fun trustAllManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    }

}
