package com.proxy.base.ui.ticket

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.proxy.base.data.TicketClose
import com.proxy.base.data.TicketItem
import com.proxy.base.databinding.ActivityTicketsBinding
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.viewBinding
import java.util.concurrent.atomic.AtomicBoolean

class TicketsActivity : BasicActivity<ActivityTicketsBinding>() {

    companion object {
        val refresh = AtomicBoolean(false)
    }

    override val binding: ActivityTicketsBinding by viewBinding()
    private val adapter by lazy { TicketsAdapter() }


    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvBack.text = "我的工单"
        binding.includeTitleBar.tvMenu.text = "新建工单"
        binding.includeTitleBar.tvMenu.setOnClickListener {
            startActivity(TicketCreateActivity::class.java)
        }

        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = adapter
        adapter.setOnTicketsAdapterListener(object : OnTicketsAdapterListener {
            override fun onClose(ticketItem: TicketItem) {
                closeTicket(ticketItem)
            }

            override fun onDetail(ticketItem: TicketItem) {
                startActivity(TicketsDetailActivity::class.java) {
                    putExtra("ID", ticketItem.id)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (refresh.compareAndSet(true, false)) {
            featList()
        }
    }

    override fun initData() {
        featList()
    }

    private fun featList() {
        showLoading()
        UserViewModel.ticketList().collectOwner(this) {
            dismissLoading()
            onSuccess {
                binding.tvEmpty.isVisible = it.isNullOrEmpty()
                adapter.submitList(it)
                binding.rvList.smoothScrollToPosition(0)
            }
            onFailed {
                binding.tvEmpty.isVisible = true
            }
        }
    }

    private fun closeTicket(item: TicketItem) {
        showLoading()
        UserViewModel.ticketClose(TicketClose("${item.id}")).collectOwner(this) {
            dismissLoading()
            onSuccess {
                featList()
            }
        }
    }
}