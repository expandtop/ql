package com.proxy.base.ui.invite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.proxy.base.data.InviteReward
import com.proxy.base.databinding.ItemInviteRecordBinding
import ex.ss.lib.tools.extension.formatSecondDate

/**
 * 2024/9/9
 */
val InviteRewardDiffCallback = object : DiffUtil.ItemCallback<InviteReward>() {
    override fun areItemsTheSame(oldItem: InviteReward, newItem: InviteReward): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: InviteReward, newItem: InviteReward): Boolean {
        return oldItem.areContentsTheSame(newItem)
    }
}

class InviteViewHolder(val binding: ItemInviteRecordBinding) : RecyclerView.ViewHolder(binding.root)

class InviteAdapterV2 : PagingDataAdapter<InviteReward, InviteViewHolder>(InviteRewardDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemInviteRecordBinding.inflate(inflater, parent, false)
        return InviteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InviteViewHolder, position: Int) {
        val data = getItem(position) ?: return
        val binding = holder.binding
        binding.tvInviteUser.text = "邀请用户：${data.email}"
        binding.tvInviteTime.text = data.created_at
    }
}