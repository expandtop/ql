package com.proxy.base.ui.message

import android.view.LayoutInflater
import android.view.ViewGroup
import com.proxy.base.data.HelpDetail
import com.proxy.base.data.Notice
import com.proxy.base.databinding.ItemHelpBinding
import com.proxy.base.databinding.ItemMessageBinding
import ex.ss.lib.base.adapter.BaseItemAdapter
import ex.ss.lib.tools.extension.formatSecondDate

class HelpAdapter : BaseItemAdapter<HelpDetail, ItemHelpBinding>() {

    override fun onBindViewHolder(binding: ItemHelpBinding, position: Int) {
        val item = getItem(position)
        binding.tvTitle.text = item.title
    }

    override fun viewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemHelpBinding {
        return ItemHelpBinding.inflate(inflater, parent, false)
    }

}