package com.proxy.base.ui.proxy

import androidx.recyclerview.widget.LinearLayoutManager
import com.proxy.base.R
import com.proxy.base.config.NodeConfig
import com.proxy.base.databinding.DialogModeBinding
import com.proxy.base.model.NodeViewModel
import com.proxy.base.model.ProxyModeItem
import com.proxy.base.proxy.ProxyMode
import com.proxy.base.ui.dialog.BasicDialog
import ex.ss.lib.base.extension.viewBinding

class ModeDialog : BasicDialog<DialogModeBinding>() {

    private val adapter by lazy { ProxyModeAdapter() }

    override val binding: DialogModeBinding by viewBinding()

    override fun initView() {
        binding.root.setOnClickListener { dismiss() }
        binding.ivClose.setOnClickListener { dismiss() }
        binding.layoutContent.setOnClickListener { }
    }

    override fun initData() {
        binding.rvMode.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMode.adapter = adapter
        binding.rvMode.itemAnimator = null
        adapter.setOnItemClick { item, position ->
            NodeViewModel.selectMode(item.mode.value, item.name)
            featModeData()
            dismiss()
        }
        featModeData()
    }

    private fun featModeData() {
        val mode = NodeConfig.proxyMode()
        val list = listOf(
            ProxyModeItem(
                getString(R.string.rule_mode),
                getString(R.string.rule_mode_desc), ProxyMode.Rule, 0, mode == ProxyMode.Rule.value
            ),
            ProxyModeItem(
                getString(R.string.global_mode),
                getString(R.string.global_mode_desc),
                ProxyMode.Global,
                0,
                mode == ProxyMode.Global.value
            ),
        )
        adapter.submitList(list)
    }

    override fun dimAmount(): Float = 0.7F

    override fun isFullWidth(): Boolean = true

    override fun isFullHeight(): Boolean = true
}