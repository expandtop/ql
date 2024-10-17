package com.proxy.base.ui.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.proxy.base.R
import com.proxy.base.databinding.ItemNodeListBinding
import com.proxy.base.databinding.ItemNodeListGroupBinding
import com.proxy.base.proxy.ProxyNodeState
import com.proxy.base.proxy.ProxyNodeStateGroup
import ex.ss.lib.base.adapter.BaseMultiAdapter
import ex.ss.lib.base.adapter.OnMultiBindHolder

class NodeListAdapter : BaseMultiAdapter() {
    init {
        register(ProxyNodeStateGroup::class.java, NodeGroupAdapter())
        register(ProxyNodeState::class.java, NodeItemAdapter())
    }
}

class NodeGroupAdapter : OnMultiBindHolder<ProxyNodeStateGroup, ItemNodeListGroupBinding> {

    override fun onBind(
        binding: ItemNodeListGroupBinding,
        data: ProxyNodeStateGroup,
        position: Int
    ) {
        binding.tvGroupName.text = data.name
        binding.ivExpandIcon.rotation = if (data.isExpand) 90F else 0F
        binding.viewLine.isVisible = position != 0
    }

    override fun onCreateViewBinding(
        inflater: LayoutInflater, parent: ViewGroup
    ): ItemNodeListGroupBinding {
        return ItemNodeListGroupBinding.inflate(inflater, parent, false)
    }

}

class NodeItemAdapter : OnMultiBindHolder<ProxyNodeState, ItemNodeListBinding> {

    override fun onBind(binding: ItemNodeListBinding, data: ProxyNodeState, position: Int) {
        binding.root.isSelected = data.isSelect
        binding.tvName.text = data.name
        binding.tvNodeProtocol.text = data.protocol
        val delay = data.delay
        when {
            delay == 0 -> {
                binding.tvNodeDelay.setBackgroundResource(R.drawable.shape_delay_bg)
                binding.tvNodeDelay.text = "测速中"
            }

            delay > 5000 || delay < 0 -> {
                binding.tvNodeDelay.setBackgroundResource(R.drawable.shape_timeout_bg)
                binding.tvNodeDelay.text = "超时"
            }

            else -> {
                binding.tvNodeDelay.setBackgroundResource(R.drawable.shape_delay_bg)
                binding.tvNodeDelay.text = "${delay}ms"
            }
        }

    }

    override fun onCreateViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
    ): ItemNodeListBinding {
        return ItemNodeListBinding.inflate(inflater, parent, false)
    }
}