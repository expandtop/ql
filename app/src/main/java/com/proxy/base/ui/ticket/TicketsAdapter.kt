package com.proxy.base.ui.ticket

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import com.proxy.base.data.TicketItem
import com.proxy.base.databinding.ItemTicketBinding
import ex.ss.lib.base.adapter.BaseItemAdapter
import ex.ss.lib.tools.extension.formatSecondDate

/**
 * 2024/10/10
 */

interface OnTicketsAdapterListener {
    fun onClose(ticketItem: TicketItem)
    fun onDetail(ticketItem: TicketItem)
}

class TicketsAdapter : BaseItemAdapter<TicketItem, ItemTicketBinding>() {

    private var listener: OnTicketsAdapterListener? = null

    fun setOnTicketsAdapterListener(listener: OnTicketsAdapterListener) {
        this.listener = listener
    }

    override fun onBindViewHolder(binding: ItemTicketBinding, position: Int) {
        val item = getItem(position)
        binding.tvTicketValue.text = item.subject
        //0低 1中 2高
        binding.tvLevelValue.text = when (item.level) {
            0 -> "低"
            1 -> "中"
            2 -> "高"
            else -> "低"
        }
        binding.tvStatusValue.text = when (item.status) {
            0 -> "已回复"
            else -> "已关闭"
        }
        binding.tvCreateTimeValue.text = item.created_at.formatSecondDate()
        binding.tvReplayValue.text = item.updated_at.formatSecondDate()
        binding.tvCancel.isInvisible = item.status != 0
        binding.tvCancel.setOnClickListener {
            listener?.onClose(item)
        }
        binding.tvCheck.setOnClickListener {
            listener?.onDetail(item)
        }
    }

    override fun viewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTicketBinding {
        return ItemTicketBinding.inflate(inflater, parent, false)
    }
}