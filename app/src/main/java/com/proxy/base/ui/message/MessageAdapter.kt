package com.proxy.base.ui.message

import android.view.LayoutInflater
import android.view.ViewGroup
import com.proxy.base.data.Notice
import com.proxy.base.databinding.ItemMessageBinding
import ex.ss.lib.base.adapter.BaseItemAdapter
import ex.ss.lib.tools.extension.formatSecondDate

class MessageAdapter : BaseItemAdapter<Notice, ItemMessageBinding>() {

    override fun onBindViewHolder(binding: ItemMessageBinding, position: Int) {
        val item = getItem(position)
        binding.tvTitle.text = item.title
        binding.tvTime.text = item.created_at.formatSecondDate("yyyy-MM-dd")
        binding.tvMessage.text = item.content
    }

    override fun viewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemMessageBinding {
        return ItemMessageBinding.inflate(inflater, parent, false)
    }

}