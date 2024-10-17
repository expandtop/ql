package com.proxy.base.ui.main

import com.proxy.base.R
import com.proxy.base.config.UserConfig
import com.proxy.base.databinding.ActivityMainBinding
import com.proxy.base.func.APPVersionManager
import com.proxy.base.model.AppViewModel
import com.proxy.base.model.NodeViewModel
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.DialogManager
import com.proxy.base.ui.dialog.showNewVersionDialog
import com.proxy.base.ui.home.HomeFragment
import com.proxy.base.util.BrowserUtils
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.viewBinding

class MainActivity : BasicActivity<ActivityMainBinding>() {

    override val binding: ActivityMainBinding by viewBinding()

    override fun initView() {
        supportFragmentManager.beginTransaction().add(R.id.frame_content, HomeFragment())
            .commitNow()
    }

    override fun initData() {
        if (UserConfig.isLogin()) {
            NodeViewModel.featSubscribe()
        }
        APPVersionManager.checkVersion(this)
    }

    override fun onResume() {
        super.onResume()
        if (UserViewModel.payForRefreshUserInfo.compareAndSet(true, false)) {
            UserViewModel.refreshUserInfo()
            NodeViewModel.featOnlySubInfo()
        }
    }

}