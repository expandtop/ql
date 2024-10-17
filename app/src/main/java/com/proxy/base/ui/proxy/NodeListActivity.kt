package com.proxy.base.ui.proxy

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.proxy.base.config.NodeConfig
import com.proxy.base.databinding.ActivityNodeListBinding
import com.proxy.base.model.NodeViewModel
import com.proxy.base.model.UserViewModel
import com.proxy.base.proxy.ProxyNodeState
import com.proxy.base.proxy.ProxyNodeStateGroup
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.util.secondExpire
import ex.ss.lib.base.adapter.data.BaseMultiItem
import ex.ss.lib.base.extension.setOnAntiViolenceClickListener
import ex.ss.lib.base.extension.viewBinding

class NodeListActivity : BasicActivity<ActivityNodeListBinding>() {

    override val binding: ActivityNodeListBinding by viewBinding()

    private val adapter by lazy { NodeListAdapter() }

    override fun initView() {
        binding.tvBack.setOnClickListener { finish() }

        binding.ivRefresh.setOnAntiViolenceClickListener {
            refreshSubscribe {
                showToastDialog("订阅已更新")
            }
        }

        binding.ivTestDelay.setOnAntiViolenceClickListener {
            NodeViewModel.testDelay()
        }

        binding.rvNodeList.layoutManager = LinearLayoutManager(this)
        binding.rvNodeList.adapter = adapter
        binding.rvNodeList.itemAnimator = null
        adapter.setOnItemClick { item, position ->
            when (item) {
                is ProxyNodeStateGroup -> {
                    NodeConfig.setGroupExpand(item.name, !item.isExpand)
                    refreshGroupExpand()
                }

                is ProxyNodeState -> {

                    NodeViewModel.selectNode(item)
                    finish()
                }
            }
        }
        refreshSubscribe()
    }

    private fun refreshSubscribe(onEnd: () -> Unit = {}) {
        showLoading()
        NodeViewModel.featSubscribe(onEnd)
    }

    override fun initData() {
        UserViewModel.userInfoLiveData.observe(this) {
            if (it != null) {
                if (it.isExpire()) {
                    showToastDialog("套餐过期")
                }
            }
        }
        NodeViewModel.nodesLiveData.observe(this) { nodeList ->
            dismissLoading()
            binding.rvNodeList.isVisible = nodeList.isNotEmpty()
            adapter.submitList(mapper(nodeList))
        }
    }

    private fun refreshGroupExpand() {
        NodeViewModel.nodesLiveData.value?.also {
            adapter.submitList(mapper(it))
        }
    }

    private fun mapper(nodeList: List<ProxyNodeState>): List<BaseMultiItem> {
        val temp = linkedMapOf<String, MutableList<ProxyNodeState>>()
        nodeList.onEach {
            if (it.name.contains("|")) {
                val split = it.name.split("|")
                val list = temp.getOrPut(split[0]) { mutableListOf() }
                list.add(it.copy(name = split[1]))
            } else {
                val list = temp.getOrPut("未分组") { mutableListOf() }
                list.add(it)
            }
        }
        return mutableListOf<BaseMultiItem>().apply {
            temp.onEach {
                val expand = NodeConfig.isGroupExpand(it.key)
                add(ProxyNodeStateGroup(it.key, expand))
                if (expand) {
                    addAll(it.value)
                }
            }
        }
    }


}
