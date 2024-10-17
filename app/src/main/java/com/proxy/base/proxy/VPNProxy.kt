package com.proxy.base.proxy

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jfdream.xvpn.vpn.Callback
import com.jfdream.xvpn.vpn.VPNManager
import com.proxy.base.config.NodeConfig
import com.proxy.base.func.NetworkLog
import ex.ss.lib.components.log.SSLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.max

object VPNProxy : CoroutineScope by MainScope() {

    private val log by lazy { SSLog.create("VPNProxy") }

    private val initialize = AtomicBoolean(false)

    private val _vpnStatus = MutableLiveData<Pair<Int, Boolean>>()
    val vpnStatus: LiveData<Pair<Int, Boolean>> = _vpnStatus

    private val listenerStatistics = AtomicBoolean(false)

    private val vpnSpeedTime = AtomicLong(0)
    private val vpnSpeedDownMax = AtomicLong(0L)
    private val vpnSpeedUpMax = AtomicLong(0L)
    private val _vpnSpeed = MutableLiveData<Pair<Long, Long>>()
    val vpnSpeed: LiveData<Pair<Long, Long>> = _vpnSpeed
    private val userToggleStatus = AtomicInteger(Callback.K_IDEL)

    fun initialize(context: Context, debug: Boolean) {
        if (initialize.compareAndSet(false, true)) {
            VPNManager.setDebug(debug)
            VPNManager.setLoggerCallback { tag, message ->
                NetworkLog.append("$tag $message")
                log.d(message)
            }
            VPNManager.sharedManager().setApplicationContext(context)
            VPNManager.sharedManager().setVPNConectionStatusCallback {
                if (_vpnStatus.value == null) {
                    firstStatusCall(it)
                }
                val toggleStatus = userToggleStatus.get()
                if (it == toggleStatus) userToggleStatus.set(Callback.K_IDEL)
                _vpnStatus.postValue(it to (toggleStatus == it))
            }
            VPNManager.sharedManager()
                .setOnVPNStatisticsCallback("V1NP3yS3d") { downloadlink, uploadlink, mdownloadlink, muploadlink ->
                    log.d("VPNStatistics:$downloadlink $uploadlink")
                    if (System.currentTimeMillis() - vpnSpeedTime.get() >= 900L) {
                        val down = vpnSpeedDownMax.get()
                        val up = vpnSpeedUpMax.get()
                        _vpnSpeed.postValue(down to up)
                        vpnSpeedTime.set(System.currentTimeMillis())
                        vpnSpeedDownMax.set(0L)
                        vpnSpeedUpMax.set(0L)
                    } else {
                        vpnSpeedDownMax.set(max(vpnSpeedDownMax.get(), downloadlink))
                        vpnSpeedUpMax.set(max(vpnSpeedUpMax.get(), uploadlink))
                    }
                }
        }
    }

    //状态第一次回调
    private fun firstStatusCall(status: Int) {
        if (status != Callback.K_Connected) {
            NodeConfig.lastStartTime = 0
        }
    }

    fun listenerStatistics() = launch {
        if (listenerStatistics.compareAndSet(false, true)) {
            while (true) {
                delay(500)
                VPNManager.sharedManager().getStatistics("V1NP3yS3d")
                if (!listenerStatistics.get()) {
                    break
                }
            }
        }
    }

    fun stopListenerStatistics() {
        listenerStatistics.set(false)
    }

    //程序计算出来新的节点之后，自动选择的，如果已经链接的，则不重新链接
    fun autoChangeNode(node: ProxyNodeState): Boolean {
        if (VPNManager.sharedManager().currentStatus == Callback.K_Connected) {
            log.d("autoChangeNode Connected")
            return false
        }
        return changeNode(node)
    }

    fun changeNode(node: ProxyNodeState): Boolean {
        if (node.same(NodeConfig.selectedProxyNode())) {
            log.d("changeNode same")
            return false
        }
        VPNManager.sharedManager().changeURL(node.url)
        return true
    }

    fun currentStatus(): Int {
        return VPNManager.sharedManager().currentStatus
    }

    fun stopTunnel() {
        VPNManager.sharedManager().stopTunnel().also {
            if (it) userToggleStatus.set(Callback.K_Disconnected)
        }
    }

    fun startTunnel(uri: String) {
        VPNManager.sharedManager().startTunnel(uri).also {
            if (it) userToggleStatus.set(Callback.K_Connected)
        }
    }

    fun setMode(isGlobal: Boolean, isRepatriate: Boolean = false) {
        VPNManager.sharedManager().setRepatriate(isRepatriate)
        VPNManager.sharedManager().setGlobalMode(isGlobal)
    }

    fun setRouterConfiguration(url: String) {
        val routeConfigList = mutableListOf<VPNRouteConfigItem>()
        url.toHttpUrlOrNull()?.host?.also { host ->
            routeConfigList.add(VPNRouteConfigItem("direct", "domain", host))
        }
        val config = VPNRouteConfig(routeConfigList).get()
        log.d("config : $config")
        VPNManager.sharedManager().setRouterConfiguration(config)
    }
}