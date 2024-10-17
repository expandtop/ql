package com.proxy.base.model

import androidx.lifecycle.MutableLiveData
import com.proxy.base.config.NodeConfig
import com.proxy.base.proxy.ProxyMode
import com.proxy.base.proxy.VPNProxy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NodeViewModel : BaseNodeViewModel() {

    val proxyModeLiveData = MutableLiveData<Pair<String, String>>()

    fun featCacheData() = launch(Dispatchers.IO) {
        featCacheProxyMode()
        featCacheNodeList()
    }

    private fun featCacheProxyMode() {
        val proxyMode = NodeConfig.proxyMode()
        val showName = NodeConfig.proxyModeName()
        updateProxyMode(proxyMode, showName)
    }

    fun selectMode(action: String, showName: String) {
        updateProxyMode(action, showName)
    }

    private fun updateProxyMode(action: String, showName: String) {
        val isGlobal = action == ProxyMode.Global.value
        val isRepatriate = action == ProxyMode.Repatriate.value
        VPNProxy.setMode(isGlobal, isRepatriate)
        NodeConfig.saveProxyMode(action)
        NodeConfig.saveProxyModeName(showName)
        proxyModeLiveData.postValue(action to showName)
    }


}