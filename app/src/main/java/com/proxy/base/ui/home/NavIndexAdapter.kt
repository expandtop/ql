package com.proxy.base.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import coil.load
import coil.transform.RoundedCornersTransformation
import com.proxy.base.data.NavIndex
import com.proxy.base.databinding.ItemWebsiteBinding
import com.proxy.base.model.AppViewModel
import ex.ss.lib.base.adapter.BaseItemAdapter
import ex.ss.lib.base.extension.dp

class NavIndexAdapter : BaseItemAdapter<NavIndex, ItemWebsiteBinding>() {
    override fun onBindViewHolder(binding: ItemWebsiteBinding, position: Int) {
        val item = getItem(position)
        AppViewModel.requireCommConfig {
            val url = it.adimageurl + item.cover
            binding.ivWebsite.load(url) {
                transformations(RoundedCornersTransformation(10F.dp))
            }
        }

        binding.tvWebsiteName.text = item.name
    }

    override fun viewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemWebsiteBinding {
        return ItemWebsiteBinding.inflate(inflater, parent, false)
    }
}