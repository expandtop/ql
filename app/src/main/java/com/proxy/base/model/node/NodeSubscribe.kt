package com.proxy.base.model.node

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import com.proxy.base.BuildConfig
import com.proxy.base.config.UserConfig
import com.proxy.base.data.SubscribeInfo
import com.proxy.base.repo.DomainChangeInterceptor
import com.proxy.base.repo.OkhttpEventListener
import com.proxy.base.repo.Remote
import com.proxy.base.repo.dynamic.DynamicConfig
import com.proxy.base.repo.dynamic.DynamicMultiDomainConfig
import com.proxy.base.util.TimeCost
import ex.ss.lib.components.log.ILogger
import ex.ss.lib.components.log.SSLog
import ex.ss.lib.net.bean.ResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

object NodeSubscribe {

    internal val subscribeInfoLiveData = MutableLiveData<SubscribeInfo?>()
    private val netLog by lazy { SSLog.create("RemoteHttp") }
    private val okHttpClient = OkHttpClient.Builder().addNetworkInterceptor(
        HttpLoggingInterceptor {
            netLog.d(it.toByteArray(Charsets.US_ASCII).toString(Charsets.UTF_8))
        }.setLevel(
            HttpLoggingInterceptor.Level.BODY
        )
    ).eventListener(OkhttpEventListener()).build()
    private val domainChange by lazy { DomainChangeInterceptor() }

    internal val nodeBlack = listOf(
        "剩余流量", "距离下次重置剩余", "套餐到期"
    )

    fun featCache() {
        subscribeInfoLiveData.postValue(UserConfig.getSubscribeInfo())
    }

    internal suspend fun resetSubscribe(): ResponseData<String> {
        return Remote.callBase { resetSubscribe() }
    }

    internal suspend fun featOnlySubInfo() = getSubscribe(false)

    internal suspend fun getSubscribe(parse: Boolean = true): List<String> {
        val responseData = Remote.callBase { getSubscribe() }
        return if (responseData.isSuccess()) {
            subscribeInfoLiveData.postValue(responseData.data)
            UserConfig.saveSubscribeInfo(responseData.data)
            if (parse) {
                val data = parseSubscribe(responseData.data.subscribe_route)
                if (!data.isNullOrEmpty()) {
                    data.lines().filter { it.isNotEmpty() }
                } else listOf()
            } else listOf()
        } else listOf()
    }


    private suspend fun parseSubscribe(url: String): String? = withContext(Dispatchers.IO) {
        val list = DynamicMultiDomainConfig.featConfigWrapper()
        for (config in list) {
            val result = parseSubscribeRetry(config, url)
            if (result != null) return@withContext result
        }
        return@withContext null
    }

    private suspend fun parseSubscribeRetry(config: DynamicConfig, url: String): String? =
        withContext(Dispatchers.IO) {
            val subUrl = domainChange.buildDynamicUrl(
                config, "${BuildConfig.BASE_URL}/${url.replace("/api/v1/", "")}".toHttpUrlOrNull()
            )
            return@withContext runCatching {
                TimeCost.costSuspend("getSubWithOkHttpClient") {
                    parseSubscribeWithOkHttpClient(
                        subUrl.toString()
                    )
                }
                    ?.takeIf { it.isNotEmpty() } ?: TimeCost.costSuspend("getSubWithConnection") {
                    parseSubscribeWithConnection(subUrl.toString())
                }
            }.getOrNull()
        }

    private suspend fun parseSubscribeWithOkHttpClient(url: String): String? =
        withContext(Dispatchers.IO) {
            return@withContext runCatching {
                okHttpClient.newCall(Request.Builder().url(url).get().build()).execute().let {
                    if (it.isSuccessful) {
                        it.body?.string()?.let { data ->
                            TimeCost.cost("Base64-OkHttpClient") {
                                Base64.decode(data, Base64.DEFAULT).decodeToString()
                            }
                        }
                    } else {
                        null
                    }
                }
            }.getOrNull()
        }

    private suspend fun parseSubscribeWithConnection(url: String): String? =
        withContext(Dispatchers.IO) {
            return@withContext runCatching {
                URL(url).openConnection().let { it as HttpURLConnection }.let {
                    it.setConnectTimeout(10_000)
                    it.setReadTimeout(10_000)
                    it.setRequestMethod("GET")
                    val responseCode = it.getResponseCode()
                    when (responseCode) {
                        HttpURLConnection.HTTP_OK, HttpURLConnection.HTTP_PARTIAL -> {
                            InputStreamReader(it.inputStream, StandardCharsets.UTF_8).readText()
                                .let { data ->
                                    TimeCost.cost("Base64-Connection") {
                                        Base64.decode(data, Base64.DEFAULT).decodeToString()
                                    }
                                }
                        }

                        else -> null
                    }.apply { it.disconnect() }
                }
            }.getOrNull()
        }

}