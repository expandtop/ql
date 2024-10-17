package com.proxy.base.ui.user

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.proxy.base.R
import com.proxy.base.data.DeviceItem
import com.proxy.base.databinding.ItemDevicesBinding
import ex.ss.lib.base.adapter.BaseItemAdapter
import ex.ss.lib.tools.extension.formatSecondDate

class DevicesAdapter : BaseItemAdapter<DeviceItem, ItemDevicesBinding>() {

    override fun onBindViewHolder(binding: ItemDevicesBinding, position: Int) {
        val item = getItem(position)
        binding.tvDeviceValue.text = item.device_name
        binding.tvIpValue.text = item.ip
        binding.tvUaValue.text = item.ua
        binding.tvLoginTimeValue.text = item.login_at.formatSecondDate()
        binding.root.setOnClickListener { }
        binding.tvAction.setOnClickListener { callItemClick(item, position) }
        if (item.isCurrent) {
            binding.tvAction.text = "当前设备,无法踢出"
            binding.tvAction.setTextColor(Color.parseColor("#CCCCCC"))
            binding.tvAction.setBackgroundResource(R.drawable.shape_fill_button)
        } else {
            binding.tvAction.text = "踢出设备"
            binding.tvAction.setTextColor(Color.WHITE)
            binding.tvAction.setBackgroundResource(R.drawable.shape_fill_red_button)
        }
    }

    override fun viewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemDevicesBinding {
        return ItemDevicesBinding.inflate(inflater, parent, false)
    }

}