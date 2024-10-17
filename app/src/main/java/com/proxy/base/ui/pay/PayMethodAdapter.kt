package com.proxy.base.ui.pay

import android.view.LayoutInflater
import android.view.ViewGroup
import com.proxy.base.data.PaymentMethod
import com.proxy.base.databinding.ItemPayMethodBinding
import ex.ss.lib.base.adapter.BaseItemAdapter

class PayMethodAdapter : BaseItemAdapter<PaymentMethod, ItemPayMethodBinding>() {

    override fun onBindViewHolder(binding: ItemPayMethodBinding, position: Int) {
        val item = getItem(position)
        binding.tvMethdName.text = "${item.name}\n${item.payment}"
        binding.tvMethdName.isSelected = item.isSelect
        binding.ivMethodCheck.isSelected = item.isSelect
    }

    override fun viewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemPayMethodBinding {
        return ItemPayMethodBinding.inflate(inflater, parent, false)
    }

}