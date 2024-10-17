package com.proxy.base.ui.user

import androidx.recyclerview.widget.LinearLayoutManager
import com.proxy.base.config.UserConfig
import com.proxy.base.data.DeviceItem
import com.proxy.base.databinding.ActivityDevicesBinding
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.viewBinding

class DevicesActivity : BasicActivity<ActivityDevicesBinding>() {

    override val binding: ActivityDevicesBinding by viewBinding()
    private val adapter by lazy { DevicesAdapter() }

    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvBack.text = "登录设备"
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = adapter
        adapter.setOnItemClick { data, pos ->
            if (data.isCurrent) {
                showToastDialog("当前设备,无法踢出")
            } else {
                removeDevice(data)
            }
        }
    }

    override fun initData() {
        featDevices()
    }

    private fun featDevices() {
        showLoading()
        UserViewModel.getDevices().collectOwner(this) {
            dismissLoading()
            onSuccess {
                val devices = it.map { entry ->
                    entry.value.copy(
                        session_id = entry.key, isCurrent = UserConfig.session_id == entry.key
                    )
                }
                adapter.submitList(devices)
            }
        }
    }

    private fun removeDevice(item: DeviceItem) {
        showLoading()
        UserViewModel.removeDevices(item.session_id).collectOwner(this) {
            dismissLoading()
            onSuccess {
                featDevices()
            }
        }
    }

}