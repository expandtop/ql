package com.proxy.base.ui.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.proxy.base.databinding.ItemUserMenuBinding
import com.proxy.base.databinding.ItemUserMenuGroupBinding
import com.proxy.base.model.UserMenu
import com.proxy.base.model.UserMenuGroup
import ex.ss.lib.base.adapter.BaseItemAdapter

class UserMenuAdapter : BaseItemAdapter<UserMenuGroup, ItemUserMenuGroupBinding>() {

    private var onMenuClick: ((UserMenu) -> Unit)? = null

    fun setOnMenuClick(onClick: (UserMenu) -> Unit) {
        this.onMenuClick = onClick
    }

    override fun onBindViewHolder(binding: ItemUserMenuGroupBinding, position: Int) {
        val adapter = UserMenuItemAdapter()
        binding.rvMenuGroup.layoutManager = LinearLayoutManager(context)
        binding.rvMenuGroup.adapter = adapter
        adapter.submitList(getItem(position).menus)
        adapter.setOnItemClick { data, _ -> onMenuClick?.invoke(data) }
    }

    override fun viewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
    ): ItemUserMenuGroupBinding {
        return ItemUserMenuGroupBinding.inflate(inflater, parent, false)
    }
}

class UserMenuItemAdapter : BaseItemAdapter<UserMenu, ItemUserMenuBinding>() {
    override fun onBindViewHolder(binding: ItemUserMenuBinding, position: Int) {
        val data = getItem(position)
        if (data.icon != 0) {
            binding.ivIcon.isVisible = true
            binding.ivIcon.load(data.icon)
        } else {
            binding.ivIcon.isVisible = false
        }
        binding.tvTitle.text = data.title
        binding.tvDesc.text = data.desc
        binding.tvDesc.setBackgroundResource(data.descBg)
        if (data.value.isNotEmpty()) {
            binding.tvValue.isVisible = true
            binding.ivRow.isVisible = false
            binding.tvValue.text = data.value
        } else {
            binding.tvValue.text = ""
            binding.tvValue.isVisible = false
            binding.ivRow.isVisible = true
        }
    }

    override fun viewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemUserMenuBinding {
        return ItemUserMenuBinding.inflate(inflater, parent, false)
    }

}