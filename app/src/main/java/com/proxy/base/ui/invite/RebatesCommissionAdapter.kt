package com.proxy.base.ui.invite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.proxy.base.data.Commission
import com.proxy.base.databinding.ItemInviteGradientListBinding
import ex.ss.lib.base.adapter.BaseItemAdapter

/**
 * 2024/9/23
 */
class RebatesCommissionAdapter : BaseItemAdapter<Commission, ItemInviteGradientListBinding>() {
    override fun onBindViewHolder(binding: ItemInviteGradientListBinding, position: Int) {
        val item = getItem(position)
        binding.tvGradientTitle.text = "${item.title} ${item.min}-${item.max}人"
        binding.tvGradientCurrent.isVisible = item.is_here == 1
        binding.tvGradientCommission.text = "${item.proportion}%"
    }

    override fun viewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
    ): ItemInviteGradientListBinding {
        return ItemInviteGradientListBinding.inflate(inflater, parent, false)
    }
}