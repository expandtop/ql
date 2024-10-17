package com.proxy.base.ui.pay

import androidx.recyclerview.widget.GridLayoutManager
import com.proxy.base.data.CouponCheckBody
import com.proxy.base.data.CouponData
import com.proxy.base.data.OrderSaveBody
import com.proxy.base.data.Plan
import com.proxy.base.data.PlanStartPayItem
import com.proxy.base.databinding.DialogStartPayBinding
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.dialog.BasicDialog
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.ui.dialog.suspendDismissLoading
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.setOnAntiViolenceClickListener
import ex.ss.lib.base.extension.viewBinding
import ex.ss.lib.tools.extension.formatCent
import java.util.concurrent.atomic.AtomicReference

class StartPayDialog : BasicDialog<DialogStartPayBinding>() {

    private val adapter by lazy { StartPayAdapter() }
    private var onStartPay: ((PlanStartPayItem, CouponData?, String) -> Unit)? = null
    private val currentPlan = AtomicReference<Plan>()
    private val currentStartPay = AtomicReference<PlanStartPayItem>()
    private val currentCouponData = AtomicReference<CouponData?>(null)

    override val binding: DialogStartPayBinding by viewBinding()

    fun onStartPay(startPay: (PlanStartPayItem, CouponData?, String) -> Unit) {
        this.onStartPay = startPay
    }

    override fun initView() {
        binding.root.setOnClickListener { dismiss() }
        binding.ivClose.setOnClickListener { dismiss() }
        binding.layoutContent.setOnClickListener { }
        binding.tvStartPay.setOnAntiViolenceClickListener {
            startPay()
        }
        binding.tvCodeVerify.setOnClickListener {
            val code = binding.etPayCode.text.toString()
            if (code.isNullOrEmpty()) {
                return@setOnClickListener
            }
            val id = currentPlan.get().id
            showLoading()
            UserViewModel.couponCheck(CouponCheckBody(id, code)).collectOwner(viewLifecycleOwner) {
                suspendDismissLoading()
                onSuccess {
                    currentCouponData.set(it)
                    updatePayInfo()
                }
            }
        }
    }

    private fun startPay() {
        val id = currentPlan.get().id
        val startPayItem = currentStartPay.get()
        val couponData = currentCouponData.get()
        showLoading()
        UserViewModel.orderSave(OrderSaveBody(id, couponData?.code, startPayItem.period))
            .collectOwner(viewLifecycleOwner) {
                suspendDismissLoading()
                onSuccess {
                    dismiss()
                    onStartPay?.invoke(startPayItem, couponData, it)
                }
            }
    }

    override fun initData() {
        binding.rvPayList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPayList.adapter = adapter
        binding.rvPayList.itemAnimator = null
        binding.tvPriceValue.text = "0¥"
        binding.tvCouponValue.text = "0"
        adapter.setOnItemClick { item, position ->
            featModeData(position)
        }
        featModeData(0)
    }

    fun updatePlan(plan: Plan) {
        currentPlan.set(plan)
    }

    private fun featModeData(position: Int) {
        val plan = currentPlan.get()
        binding.tvModeTitle.text = plan.name
        val list = buildStartPlan(plan).mapIndexed { index, item ->
            item.copy(isSelect = index == position)
        }
        list.firstOrNull { it.isSelect }?.also {
            currentStartPay.set(it)
            updatePayInfo()
        }
        adapter.submitList(list)
    }

    private fun updatePayInfo() {
        val payItem = currentStartPay.get()
        val planId = currentPlan.get().id
        val couponData = currentCouponData.get()?.let {
            if (it.limit_plan_ids.isNullOrEmpty() || !it.limit_plan_ids.contains(planId)) {
                showToastDialog("当前订阅不用")
                currentCouponData.set(null)
                null
            } else if (it.limit_period.isNullOrEmpty() || !it.limit_period.contains(payItem.period)) {
                showToastDialog("当前套餐不用")
                currentCouponData.set(null)
                null
            } else it
        }
        binding.tvCouponValue.text = couponData?.show() ?: "0"
        val price = couponData?.discount(payItem.price) ?: payItem.price
        binding.tvPriceValue.text = "${payItem.price.formatCent()}¥"
        binding.tvStartPay.text = "确认付款 ${price.formatCent()}¥"
    }

    private fun buildStartPlan(plan: Plan): List<PlanStartPayItem> {
        val list = mutableListOf<PlanStartPayItem>()
        if (plan.month_price != null) {
            list.add(PlanStartPayItem("1个月", plan.month_price, "month_price", plan.id, false))
        }
        if (plan.quarter_price != null) {
            list.add(
                PlanStartPayItem(
                    "3个月", plan.quarter_price, "quarter_price", plan.id, false
                )
            )
        }
        if (plan.half_year_price != null) {
            list.add(
                PlanStartPayItem(
                    "6个月", plan.half_year_price, "half_year_price", plan.id, false
                )
            )
        }
        if (plan.year_price != null) {
            list.add(PlanStartPayItem("1年", plan.year_price, "year_price", plan.id, false))
        }
        if (plan.two_year_price != null) {
            list.add(
                PlanStartPayItem(
                    "2年", plan.two_year_price, "two_year_price", plan.id, false
                )
            )
        }
        if (plan.three_year_price != null) {
            list.add(
                PlanStartPayItem(
                    "3年", plan.three_year_price, "three_year_price", plan.id, false
                )
            )
        }
        return list
    }
}