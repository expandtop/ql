package com.proxy.base.ui.ticket

import android.app.Activity
import androidx.appcompat.widget.PopupMenu
import com.proxy.base.data.TicketSave
import com.proxy.base.databinding.ActivityTicketCreateBinding
import com.proxy.base.databinding.ActivityTicketsBinding
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.ui.ticket.TicketsActivity.Companion.refresh
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.viewBinding
import java.util.concurrent.atomic.AtomicInteger

class TicketCreateActivity : BasicActivity<ActivityTicketCreateBinding>() {

    override val binding: ActivityTicketCreateBinding by viewBinding()
    private val level = AtomicInteger(0)

    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvBack.text = "新建工单"

        binding.tvSubmit.setOnClickListener {
            ticketSave()
        }

        binding.tvInputLevel.setOnClickListener {
            PopupMenu(this, binding.tvInputLevel).apply {
                menu.add("低")
                menu.add("中")
                menu.add("高")
                setOnMenuItemClickListener {
                    binding.tvInputLevel.text = it.title
                    when (it.title) {
                        "低" -> level.set(0)
                        "中" -> level.set(1)
                        "高" -> level.set(2)
                        else -> level.set(0)
                    }
                    true
                }
                show()
            }
        }
    }

    private fun ticketSave() {
        val type = binding.etInputType.text.toString()
        if (type.isEmpty()) {
            showToastDialog(binding.etInputType.hint.toString())
            return
        }
        val content = binding.etInputContent.text.toString()
        if (content.isEmpty()) {
            showToastDialog(binding.etInputContent.hint.toString())
            return
        }
        val save = TicketSave(subject = type, level = level.get(), message = content)
        showLoading()
        UserViewModel.ticketSave(save).collectOwner(this) {
            dismissLoading()
            onSuccess {
                showToastDialog(success.msg).setOnDismissCallback {
                    finish()
                    refresh.set(true)
                }
            }
        }
    }


    override fun initData() {
    }
}