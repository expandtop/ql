package com.proxy.base.ui.pay

import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.proxy.base.data.OrderCancelBody
import com.proxy.base.data.OrderCheckoutBody
import com.proxy.base.data.PayOrder
import com.proxy.base.databinding.ActivityOrderBinding
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.toast
import ex.ss.lib.base.extension.viewBinding
import ex.ss.lib.tools.common.BrowserTools
import ex.ss.lib.tools.extension.formatCent
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class OrderActivity : BasicActivity<ActivityOrderBinding>() {

    override val binding: ActivityOrderBinding by viewBinding()
    private val orderAdapter by lazy { OrderAdapter() }
    private val needRefresh = AtomicBoolean(false)

    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvBack.text = "我的订单"
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.itemAnimator = null
        binding.rvList.adapter = orderAdapter
        orderAdapter.setOnOrderAction(object : OnOrderAction {
            override fun onOrderPay(item: PayOrder) {
                continueOrderPay(item)
            }

            override fun onOrderClose(item: PayOrder) {
                orderCancel(item.trade_no)
            }

        })
        orderAdapter.loadStateFlow.collectOwner(this) {
            binding.ivEmpty.isVisible = orderAdapter.itemCount <= 0
        }
    }

    override fun initData() {
        UserViewModel.orderPaging.collectOwner(this) {
            orderAdapter.submitData(this)
        }
    }

    override fun onResume() {
        super.onResume()
        if (needRefresh.compareAndSet(true, false)) {
            orderAdapter.refresh()
        }
    }

    private fun continueOrderPay(item: PayOrder) {
        val paymentId = item.payment_id
        if (paymentId != null) {
            realPay(item.trade_no, paymentId)
        } else {
            showPayMethod(item)
        }
    }

    private fun showPayMethod(order: PayOrder) {
        PayMethodDialog().apply {
            updateInfo(
                planName = order.plan.name,
                payPrice = order.plan.getPrice(order.period),
                couponPrice = order.discount_amount ?: 0,
                couponShow = order.discount_amount?.formatCent()?.let { "$it¥" } ?: "0",
                order = order.trade_no
            )
        }.show(supportFragmentManager, "PayMethodDialog")
    }

    private fun realPay(order: String, methodId: Int) {
        showLoading()
        UserViewModel.orderCheckout(OrderCheckoutBody(order, methodId)).collectOwner(this) {
            dismissLoading()
            onSuccess {
                BrowserTools.openBrowser(this@OrderActivity, it.data)
                needRefresh.set(true)
            }
        }
    }

    private fun orderCancel(order: String) {
        showLoading()
        UserViewModel.orderCancel(OrderCancelBody(order)).collectOwner(this) {
            dismissLoading()
            onSuccess {
                orderAdapter.refresh()
            }
        }
    }

}