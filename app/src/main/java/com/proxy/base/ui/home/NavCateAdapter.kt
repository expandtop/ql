package com.proxy.base.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.proxy.base.data.NavCate
import com.proxy.base.databinding.ItemNavCateBinding
import ex.ss.lib.base.adapter.BaseItemAdapter

class NavCateAdapter : BaseItemAdapter<NavCate, ItemNavCateBinding>() {
    override fun onBindViewHolder(binding: ItemNavCateBinding, position: Int) {
        val item = getItem(position)
        binding.tvTitle.text = item.name
    }

    override fun viewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemNavCateBinding {
        return ItemNavCateBinding.inflate(inflater, parent, false)
    }
}