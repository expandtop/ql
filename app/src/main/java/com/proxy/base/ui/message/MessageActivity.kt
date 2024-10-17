package com.proxy.base.ui.message

import androidx.recyclerview.widget.LinearLayoutManager
import com.proxy.base.databinding.ActivityMessageBinding
import com.proxy.base.model.AppViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.viewBinding

class MessageActivity : BasicActivity<ActivityMessageBinding>() {

    override val binding: ActivityMessageBinding by viewBinding()
    private val messageAdapter by lazy { MessageAdapter() }

    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvBack.text = "公告"
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = messageAdapter
    }

    override fun initData() {
        showLoading()
        AppViewModel.getNotice().collectOwner(this) {
            dismissLoading()
            onSuccess { data ->
                messageAdapter.submitList(data.data)
            }
        }
    }

}