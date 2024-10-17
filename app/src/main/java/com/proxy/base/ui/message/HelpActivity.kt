package com.proxy.base.ui.message

import androidx.recyclerview.widget.LinearLayoutManager
import com.proxy.base.databinding.ActivityHelpBinding
import com.proxy.base.model.AppViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.viewBinding

class HelpActivity : BasicActivity<ActivityHelpBinding>() {

    override val binding: ActivityHelpBinding by viewBinding()
    private val adapter by lazy { HelpAdapter() }

    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvBack.text = "帮助中心"
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = adapter
        adapter.setOnItemClick { data, pos ->
            startActivity(HelpDetailActivity::class.java) {
                putExtra("TITLE", data.title)
                putExtra("CONTENT", data.content)
            }
        }
    }

    override fun initData() {
        showLoading()
        AppViewModel.getHelp().collectOwner(this) {
            dismissLoading()
            onSuccess {
                adapter.submitList(it)
            }
        }
    }

}