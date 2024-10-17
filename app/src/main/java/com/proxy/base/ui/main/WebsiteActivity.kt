package com.proxy.base.ui.main

import androidx.recyclerview.widget.GridLayoutManager
import com.proxy.base.databinding.ActivityWebsiteBinding
import com.proxy.base.model.AppViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.ui.home.NavIndexAdapter
import com.proxy.base.util.BrowserUtils
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.viewBinding

class WebsiteActivity : BasicActivity<ActivityWebsiteBinding>() {

    override val binding: ActivityWebsiteBinding by viewBinding()

    private val adapter by lazy { NavIndexAdapter() }

    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvBack.text = intent.getStringExtra("TITLE") ?: "常用网址"

        binding.rvRecordList.layoutManager = GridLayoutManager(this, 4)
        binding.rvRecordList.adapter = adapter

    }

    override fun initData() {
        val id = intent.getIntExtra("ID", -1)
        if (id == null || id == -1) finish()
        showLoading()
        AppViewModel.navIndex(id).collectOwner(this) {
            dismissLoading()
            onSuccess {
                adapter.submitList(it.data)
            }
        }
        adapter.setOnItemClick { data, pos ->
            BrowserUtils.openBrowser(this, data.link)
        }
    }


}