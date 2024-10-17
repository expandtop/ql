package com.proxy.base.ui.invite

import androidx.recyclerview.widget.LinearLayoutManager
import com.proxy.base.R
import com.proxy.base.databinding.ActivityInviteRecordBinding
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.service.AppServer
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.viewBinding

class InviteRecordActivity : BasicActivity<ActivityInviteRecordBinding>() {

    override val binding: ActivityInviteRecordBinding by viewBinding()
    private val adapter by lazy { InviteAdapterV2() }

    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvBack.text = "我的邀请记录"
        binding.includeTitleBar.ivMenu.setImageResource(R.drawable.login_service)
        binding.includeTitleBar.ivMenu.setOnClickListener {
            AppServer.open(this)
        }

        binding.rvRecordList.layoutManager = LinearLayoutManager(this)
        binding.rvRecordList.adapter = adapter
    }

    override fun initData() {
        UserViewModel.inviteRecordLiveData.observe(this) {
            binding.tvInviteInfo.text = "共计${it.total}人"
        }
        UserViewModel.invitePaging.collectOwner(this) {
            adapter.submitData(this)
        }
    }


}