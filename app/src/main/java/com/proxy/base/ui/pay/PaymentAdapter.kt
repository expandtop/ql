package com.proxy.base.ui.pay

import android.view.LayoutInflater
import android.view.ViewGroup
import com.proxy.base.data.PlanPayItem
import com.proxy.base.databinding.ItemPaymentBinding
import ex.ss.lib.base.adapter.BaseItemAdapter
import ex.ss.lib.tools.extension.formatCent

class PaymentAdapter : BaseItemAdapter<PlanPayItem, ItemPaymentBinding>() {

    override fun onBindViewHolder(binding: ItemPaymentBinding, position: Int) {
        val item = getItem(position)
        binding.ivBg.isSelected = item.isSelect
        binding.tvTitle.text = item.name
        binding.tvValue.text = item.price.formatCent()
    }

    override fun viewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemPaymentBinding {
        return ItemPaymentBinding.inflate(inflater, parent, false)
    }

}