package com.proxy.base.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.proxy.base.config.NodeConfig
import com.proxy.base.data.SubscribeInfo
import com.proxy.base.model.node.NodeStorage
import com.proxy.base.model.node.NodeSubscribe
import com.proxy.base.proxy.ProxyNodeState
import com.proxy.base.proxy.VPNProxy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseNodeViewModel : BaseViewModel() {
    val currentNodeLiveData: LiveData<ProxyNodeState?> = NodeSelector.currentNodeLiveData
    val nodesLiveData: LiveData<List<ProxyNodeState>> = NodeSelector.nodesLiveData
    val subscribeInfoLiveData: MutableLiveData<SubscribeInfo?> = NodeSubscribe.subscribeInfoLiveData

    init {
        launch {
            NodeStorage.nodeResultLiveData.observeForever { result ->
                result.list?.also { NodeSelector.autoSelect(it, result.resetSelect) }
            }
        }
    }

    fun testDelay() = launch {
        nodesLiveData.value?.also { proxies ->
            NodeStorage.testNodeDelay(proxies)
        }
    }

    fun featOnlySubInfo() = launch(Dispatchers.IO) {
        NodeSubscribe.featOnlySubInfo()
    }

    fun featSubscribe(onEnd: () -> Unit = {}) = launch(Dispatchers.IO) {
        NodeStorage.featNodeList()
        withContext(Dispatchers.Main) {
            onEnd.invoke()
        }
    }

    fun selectNode(node: ProxyNodeState) {
        NodeSelector.selectNode(node)
    }

    internal fun featCacheNodeList() {
        NodeStorage.getCacheNode()
    }

}

internal object NodeSelector {

    internal val nodesLiveData = MutableLiveData<List<ProxyNodeState>>()
    internal val currentNodeLiveData = MutableLiveData<ProxyNodeState?>()

    private val currentNodes = mutableListOf<ProxyNodeState>()

    internal fun autoSelect(nodes: List<ProxyNodeState>, force: Boolean = false) =
        syncCurrent(nodes) {
            val selectedProxyNode = NodeConfig.selectedProxyNode()
            if (force || selectedProxyNode == null) {
                selectDefault(it, true)
            } else {
                selectCurrent(selectedProxyNode, it, true)
            }
        }

    internal fun selectNode(node: ProxyNodeState) = syncCurrent {
        selectCurrent(node, it, false)
    }

    /**
     * 选中之前选择的节点,未找到则选择第一个节点
     */
    private fun selectCurrent(
        node: ProxyNodeState,
        nodes: List<ProxyNodeState>,
        isAutoSelect: Boolean,
    ) {
        val selectNode = nodes.firstOrNull { it.same(node) }
        if (selectNode != null) {
            updateNodeSelect(selectNode, nodes, isAutoSelect)
        } else {
            selectDefault(nodes, isAutoSelect)
        }
    }

    /**
     * 选择默认节点,选择第一个节点
     */
    private fun selectDefault(nodes: List<ProxyNodeState>, isAutoSelect: Boolean) {
        val selectNode = nodes.minByOrNull { it.delay }
        updateNodeSelect(selectNode, nodes, isAutoSelect)
    }

    private fun updateNodeSelect(
        node: ProxyNodeState?,
        nodes: List<ProxyNodeState>,
        isAutoSelect: Boolean,
    ) {
        val result = if (node != null) {
            if (isAutoSelect) {
                VPNProxy.autoChangeNode(node)
            } else {
                VPNProxy.changeNode(node)
            }
            nodes.map { it.copy(isSelect = it.same(node)) }
        } else nodes
        NodeConfig.saveSelectedProxyNode(node)
        nodesLiveData.postValue(result)
        currentNodeLiveData.postValue(node)
//        node?.also {
//            ProxyMonitor.start(it.type())
//        }
    }

    private fun syncCurrent(
        list: List<ProxyNodeState>? = null,
        block: (List<ProxyNodeState>) -> Unit,
    ) {
        synchronized(currentNodes) {
            if (list != null) {
                currentNodes.clear()
                currentNodes.addAll(list)
            }
            block.invoke(currentNodes)
        }
    }

}