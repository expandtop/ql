package com.proxy.base.ui.user

import com.proxy.base.databinding.ActivityMineBinding
import com.proxy.base.ui.BasicActivity
import ex.ss.lib.base.activity.BaseActivity
import ex.ss.lib.base.extension.viewBinding

class MineActivity : BasicActivity<ActivityMineBinding>() {

    override val binding: ActivityMineBinding by viewBinding()

    override fun initView() {
        supportFragmentManager.beginTransaction()
            .add(binding.frameContent.id, UserFragment())
            .commitNow()
    }

    override fun initData() {

    }
}