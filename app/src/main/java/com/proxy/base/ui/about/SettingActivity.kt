package com.proxy.base.ui.about

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proxy.base.R
import com.proxy.base.databinding.ActivityAboutBinding
import com.proxy.base.databinding.ActivitySettingBinding
import com.proxy.base.func.APPVersionManager
import com.proxy.base.model.NodeViewModel
import com.proxy.base.model.UserMenu
import com.proxy.base.model.UserMenuGroup
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.ui.user.UserMenuAdapter
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.toast
import ex.ss.lib.base.extension.viewBinding

/**
 * 2024/7/24
 */
class SettingActivity : BasicActivity<ActivitySettingBinding>() {

    override val binding: ActivitySettingBinding by viewBinding()

    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvBack.text = "设置"

        val menus = mutableListOf<UserMenu>().apply {
            add(UserMenu(0, 0, "更新订阅"))
            add(UserMenu(1, 0, "检查更新"))
            add(UserMenu(2, 0, "关于"))
        }
        initMenu(binding.rvAboutMenu, menus) {
            when (it.type) {
                0 -> {
                    NodeViewModel.featSubscribe {
                        showToastDialog("订阅已更新")
                    }
                }

                1 -> {
                    APPVersionManager.checkVersion(this, true)
                }

                2 -> {
                    startActivity(AboutActivity::class.java)
                }
            }
        }
    }


    private fun initMenu(
        recyclerView: RecyclerView, menus: List<UserMenu>, onMenuClick: (UserMenu) -> Unit,
    ) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = UserMenuAdapter().apply {
            submitList(listOf(UserMenuGroup(menus)))
            setOnMenuClick(onMenuClick)
        }
    }

    override fun initData() {
    }
}