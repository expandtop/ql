package com.proxy.base.ui.message

import android.text.Html
import com.proxy.base.databinding.ActivityHelpDetailBinding
import com.proxy.base.ui.BasicActivity
import ex.ss.lib.base.extension.viewBinding

class HelpDetailActivity : BasicActivity<ActivityHelpDetailBinding>() {

    override val binding: ActivityHelpDetailBinding by viewBinding()

    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvBack.text = intent.getStringExtra("TITLE")
    }

    override fun initData() {
        val content = intent.getStringExtra("CONTENT")
        binding.tvContent.text = Html.fromHtml(content)
    }

}