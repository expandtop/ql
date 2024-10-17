package com.proxy.base.ui.pay

import androidx.recyclerview.widget.LinearLayoutManager
import com.proxy.base.data.OrderCheckoutBody
import com.proxy.base.data.PaymentMethod
import com.proxy.base.databinding.DialogPayMethodBinding
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.dialog.BasicDialog
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.dialog.BaseDialog
import ex.ss.lib.base.extension.setOnAntiViolenceClickListener
import ex.ss.lib.base.extension.toast
import ex.ss.lib.base.extension.viewBinding
import ex.ss.lib.tools.common.BrowserTools
import ex.ss.lib.tools.extension.formatCent
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class PayMethodDialog : BasicDialog<DialogPayMethodBinding>() {

    private val adapter by lazy { PayMethodAdapter() }
    override val binding: DialogPayMethodBinding by viewBinding()


    private val planName = AtomicReference("")
    private val payPrice = AtomicInteger(0)
    private val couponPrice = AtomicInteger(0)
    private val couponShow = AtomicReference("")
    private val currentOrder = AtomicReference<String>("")
    private val currentPayMethod = AtomicReference<PaymentMethod>(null)

    override fun initView() {
        binding.root.setOnClickListener { dismiss() }
        binding.ivClose.setOnClickListener { dismiss() }
        binding.layoutContent.setOnClickListener { }
        binding.tvPay.setOnAntiViolenceClickListener {
            startPay()
        }
    }

    private fun startPay() {
        val order = currentOrder.get()
        val method = currentPayMethod.get()
        showLoading()
        UserViewModel.orderCheckout(OrderCheckoutBody(order, method.id))
            .collectOwner(viewLifecycleOwner) {
                dismissLoading()
                onSuccess {
                    dismiss()
                    BrowserTools.openBrowser(requireContext(), it.data)
                }
            }

    }

    override fun initData() {
        binding.rvPayMethodList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPayMethodList.adapter = adapter
        binding.rvPayMethodList.itemAnimator = null
        adapter.setOnItemClick { item, position ->
            currentPayMethod.set(item)
            showMethod(adapter.currentList)
            showPayInfo()
        }
        featModeData()
        showPayInfo()
    }


    private fun showPayInfo() {
        binding.tvProductValue.text = planName.get()
        val payPrice = payPrice.get()
        binding.tvPriceValue.text = payPrice.formatCent() + "¥"
        binding.tvCouponValue.text = couponShow.get()
        val realPayPrice = maxOf(0, payPrice - couponPrice.get())
        val feePrice = currentPayMethod.get()?.fee(realPayPrice) ?: 0
        binding.tvChargeValue.text = feePrice.formatCent() + "¥"
        binding.tvPay.text = (feePrice + realPayPrice).formatCent() + "¥"
    }

    /**
     * @param planName 订阅名称
     * @param payPrice 支付价格
     * @param couponPrice 优惠价格
     * @param couponShow 优惠显示
     * @param order 订单ID
     */
    fun updateInfo(
        planName: String, payPrice: Int, couponPrice: Int, couponShow: String, order: String,
    ) {
        this.planName.set(planName)
        this.payPrice.set(payPrice)
        this.couponPrice.set(couponPrice)
        this.couponShow.set(couponShow)
        currentOrder.set(order)
    }

    private fun featModeData() {
        UserViewModel.getPaymentMethod().collectOwner(viewLifecycleOwner) {
            onSuccess {
                showMethod(it)
            }
        }
    }

    private fun showMethod(it: List<PaymentMethod>) {
        val list = it.mapIndexed { index, method ->
            if (currentPayMethod.get() == null) {
                method.copy(isSelect = index == 0)
            } else {
                method.copy(isSelect = currentPayMethod.get().id == method.id)
            }
        }.also {
            it.firstOrNull { item -> item.isSelect }?.also { item ->
                currentPayMethod.set(item)
                showPayInfo()
            }
        }
        adapter.submitList(list)
    }

}