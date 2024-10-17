package com.proxy.base.ui.user

import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proxy.base.R
import com.proxy.base.config.UserConfig
import com.proxy.base.databinding.ActivityAccountBinding
import com.proxy.base.model.UserMenu
import com.proxy.base.model.UserMenuGroup
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.ui.service.AppServer
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.viewBinding

class AccountActivity : BasicActivity<ActivityAccountBinding>() {

    override val binding: ActivityAccountBinding by viewBinding()

    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvBack.text = "账户"

        binding.tvLoginOut.isVisible = UserConfig.isAccountLogin()
        binding.tvLoginOut.setOnClickListener {
            showLoading()
            UserViewModel.loginOut().collectOwner(this) {
                dismissLoading()
                loginExpire()
            }
        }

        binding.tvContactUs.setOnClickListener {
            AppServer.open(this)
        }

        initAccountMenu()
        UserViewModel.userInfoLiveData.observe(this) {
            initAccountMenu()
        }
    }

    private fun initAccountMenu() {
        val menus = mutableListOf<UserMenu>().apply {
            if (UserConfig.isAccountLogin()) {
                add(UserMenu(0, 0, "修改密码"))
            }
            if (UserConfig.isAccountLogin()) {
                add(
                    UserMenu(
                        1, 0, "绑定邮箱", "已绑定", R.drawable.shape_binded_email_bg
                    )
                )
            } else {
                add(
                    UserMenu(
                        1, 0, "绑定邮箱", "绑定邮箱赠送时长", R.drawable.shape_user_menu_desc_bg
                    )
                )
            }
            if (UserConfig.isLogin()) {
                add(UserMenu(2, 0, "登录设备"))
            }
            if (UserConfig.isAccountLogin()) {
                add(UserMenu(3, 0, "注销账户"))
            }
        }
        initMenu(binding.rvAccountMenu, menus) {
            when (it.type) {
                0 -> {
                    startActivity(ResetPwdActivity::class.java)
                }

                1 -> {
                    if (UserConfig.isAccountLogin()) {
                        showToastDialog("已绑定")
                    } else {
                        startActivity(BindEmailActivity::class.java)
                    }
                }

                2 -> {
                    startActivity(DevicesActivity::class.java)
                }

                3 -> {
                    startActivity(CloseUserActivity::class.java)
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