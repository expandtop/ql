package com.proxy.base.ui.pay

import android.view.LayoutInflater
import android.view.ViewGroup
import com.proxy.base.data.PlanStartPayItem
import com.proxy.base.databinding.ItemStartPayBinding
import ex.ss.lib.base.adapter.BaseItemAdapter
import ex.ss.lib.tools.extension.formatCent

class StartPayAdapter : BaseItemAdapter<PlanStartPayItem, ItemStartPayBinding>() {
    override fun onBindViewHolder(binding: ItemStartPayBinding, position: Int) {
        val item = getItem(position)
        binding.tvName.text = item.name
        binding.tvDesc.text = "${item.price.formatCent()}¥"
        binding.root.isSelected = item.isSelect
        binding.tvName.isSelected = item.isSelect
    }

    override fun viewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemStartPayBinding {
        return ItemStartPayBinding.inflate(inflater, parent, false)
    }
}