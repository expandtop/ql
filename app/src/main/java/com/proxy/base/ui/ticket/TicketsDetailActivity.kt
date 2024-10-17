package com.proxy.base.ui.ticket

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.proxy.base.data.TicketClose
import com.proxy.base.data.TicketItem
import com.proxy.base.data.TicketReply
import com.proxy.base.databinding.ActivityTicketDetailBinding
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.util.EPSoftKeyBoardListener
import com.proxy.base.util.EPSoftKeyBoardWrapper
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.viewBinding

class TicketsDetailActivity : BasicActivity<ActivityTicketDetailBinding>() {

    override val binding: ActivityTicketDetailBinding by viewBinding()
    private val adapter by lazy { TicketsDetailAdapter() }
    private val ticketId by lazy { intent.getIntExtra("ID", -1) }

    override fun initView() {
        EPSoftKeyBoardWrapper.register(this, binding)
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvBack.text = "工单回复"

        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = adapter

        binding.tvSend.setOnClickListener {
            val content = binding.etInputContent.text.toString()
            if (content.isNullOrEmpty()) {
                showToastDialog(binding.etInputContent.hint.toString())
                return@setOnClickListener
            }
            replayTicket(content)
        }
    }

    override fun initData() {
        if (ticketId < 0) {
            showToastDialog("参数错误").setOnDismissCallback {
                finish()
            }
        } else {
            featDetail()
        }
    }

    private fun featDetail() {
        showLoading()
        UserViewModel.ticketDetail("$ticketId").collectOwner(this) {
            dismissLoading()
            onSuccess {
                binding.tvEmpty.isVisible = it.message.isNullOrEmpty()
                adapter.submitList(it.message ?: listOf())
            }
            onFailed {
                binding.tvEmpty.isVisible = true
            }
        }
    }

    private fun replayTicket(content: String) {
        showLoading()
        UserViewModel.ticketReplay(TicketReply("$ticketId", content)).collectOwner(this) {
            dismissLoading()
            onSuccess {
                featDetail()
            }
        }
    }
}