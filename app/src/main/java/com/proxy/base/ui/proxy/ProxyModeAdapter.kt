package com.proxy.base.ui.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import com.proxy.base.R
import com.proxy.base.databinding.ItemModeListBinding
import com.proxy.base.model.ProxyModeItem
import ex.ss.lib.base.adapter.BaseItemAdapter
import ex.ss.lib.base.extension.textColor

class ProxyModeAdapter : BaseItemAdapter<ProxyModeItem, ItemModeListBinding>() {
    override fun onBindViewHolder(binding: ItemModeListBinding, position: Int) {
        val item = getItem(position)
        binding.tvName.text = item.name
        binding.tvDesc.text = item.desc
        binding.root.isSelected = item.isSelect
        binding.tvName.isSelected = item.isSelect
    }

    override fun viewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemModeListBinding {
        return ItemModeListBinding.inflate(inflater, parent, false)
    }
}