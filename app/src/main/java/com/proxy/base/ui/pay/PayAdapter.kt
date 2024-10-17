package com.proxy.base.ui.pay

import android.view.LayoutInflater
import android.view.ViewGroup
import com.proxy.base.data.Plan
import com.proxy.base.databinding.ItemPayBinding
import ex.ss.lib.base.adapter.BaseItemAdapter
import ex.ss.lib.tools.extension.formatCent

class PayAdapter : BaseItemAdapter<Plan, ItemPayBinding>() {

    override fun onBindViewHolder(binding: ItemPayBinding, position: Int) {
        binding.root.setOnClickListener { }
        val item = getItem(position)
        binding.tvTitle.text = item.name
        binding.tvDesc.text = item.content
        binding.tvPrice.text = getShowPrice(item)
        binding.tvSubscribe.setOnClickListener { callItemClick(item, position) }
    }

    private fun getShowPrice(plan: Plan): String {
        val monthPrice = when {
            plan.month_price != null -> plan.month_price
            plan.quarter_price != null -> plan.quarter_price / 3
            plan.half_year_price != null -> plan.half_year_price / 6
            plan.year_price != null -> plan.year_price / 12
            plan.two_year_price != null -> plan.two_year_price / 24
            plan.three_year_price != null -> plan.three_year_price / 36
            else -> 0
        }
        return "${monthPrice.formatCent()}¥"
    }

    override fun viewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemPayBinding {
        return ItemPayBinding.inflate(inflater, parent, false)
    }

}