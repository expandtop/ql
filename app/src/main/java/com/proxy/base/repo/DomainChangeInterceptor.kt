package com.proxy.base.repo

import com.proxy.base.BuildConfig
import com.proxy.base.proxy.VPNProxy
import com.proxy.base.repo.dynamic.DynamicConfig
import com.proxy.base.repo.dynamic.DynamicMultiDomainConfig
import ex.ss.lib.components.log.SSLog
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.random.Random

class DomainChangeInterceptor : Interceptor {

    private val log by lazy { SSLog.create("DomainChangeInterceptor") }

    override fun intercept(chain: Interceptor.Chain): Response {
        return retryProceed(chain) {
            val request = chain.request().newBuilder().url(it).build()
            chain.proceed(request)
        }
    }

    private fun retryProceed(chain: Interceptor.Chain, onProceed: (HttpUrl) -> Response): Response {
        val list = DynamicMultiDomainConfig.featConfigWrapper()
        for (config in list) {
            val dynamicUrl = buildDynamicUrl(config, chain.request().url) ?: chain.request().url
            runCatching {
                 val response = onProceed(dynamicUrl)
                return response
            }.getOrElse {
                log.d("retryProceed : $it")
            }
        }
        return onProceed(chain.request().url)
    }

    fun buildDynamicUrl(config: DynamicConfig, url: HttpUrl?): HttpUrl? {
        return if (config.success) {
            VPNProxy.setRouterConfiguration(config.new)
            val httpUrl = replaceHttpUrl(url, config.new, BuildConfig.BASE_URL)
            log.d("replaceHttpUrl - oldUrl : $url newHost : ${config.new} baseHost : ${BuildConfig.BASE_URL} newHttpUrl:$httpUrl")
            if (httpUrl != null) {
                return httpUrl
            } else {
                return url
            }
        } else {
            return url
        }
    }

    private fun replaceHttpUrl(oldHttpUrl: HttpUrl?, newHost: String, baseHost: String): HttpUrl? {
        val newBuild = oldHttpUrl?.newBuilder()
        newHost.toHttpUrlOrNull()?.also { new -> //remove all pathSegment
            oldHttpUrl?.pathSegments?.onEach { newBuild?.removePathSegment(0) } //replace new scheme
            newBuild?.scheme(new.scheme) //replace new host
            newBuild?.host(new.host) //replace new port
            newBuild?.port(new.port) //add new host pathSegments
            new.pathSegments.filter { item -> item.isNotEmpty() }
                .onEach { path -> newBuild?.addPathSegment(path) } //remove old host pathSegments and add remainder pathSegments
            oldHttpUrl?.pathSegments?.filter { path ->
                baseHost.toHttpUrlOrNull()?.pathSegments?.contains(path) != true
            }?.filter { item -> item.isNotEmpty() }
                ?.onEach { path -> newBuild?.addPathSegment(path) }
        }
        return newBuild?.build()
    }

}