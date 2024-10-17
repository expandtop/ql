package com.proxy.base.ui.ticket

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.proxy.base.data.TicketItemMessage
import com.proxy.base.databinding.ItemTicketDetailBinding
import ex.ss.lib.base.adapter.BaseItemAdapter

/**
 * 2024/10/10
 */


class TicketsDetailAdapter : BaseItemAdapter<TicketItemMessage, ItemTicketDetailBinding>() {


    override fun onBindViewHolder(binding: ItemTicketDetailBinding, position: Int) {
        val item = getItem(position)
        binding.tvMessage.isVisible = item.is_me
        binding.tvReply.isVisible = !item.is_me
        if (item.is_me) {
            binding.tvMessage.text = item.message
            binding.tvReply.text = ""
        } else {
            binding.tvMessage.text = ""
            binding.tvReply.text = item.message
        }

    }

    override fun viewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTicketDetailBinding {
        return ItemTicketDetailBinding.inflate(inflater, parent, false)
    }
}