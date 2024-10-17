package com.proxy.base.ui.pay

import com.proxy.base.databinding.ActivityPayBinding
import com.proxy.base.ui.BasicActivity
import ex.ss.lib.base.extension.viewBinding

/**
 * 2024/9/5
 */
class PayActivity : BasicActivity<ActivityPayBinding>() {

    override val binding: ActivityPayBinding by viewBinding()

    override fun initView() {
        supportFragmentManager.beginTransaction()
            .add(binding.frameContent.id, PayFragment())
            .commitNow()
    }

    override fun initData() {


    }

}