package com.proxy.base.ui.pay

import androidx.recyclerview.widget.LinearLayoutManager
import com.proxy.base.data.CouponData
import com.proxy.base.data.Plan
import com.proxy.base.data.PlanStartPayItem
import com.proxy.base.databinding.FragmentPayBinding
import com.proxy.base.model.AppViewModel
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.toast
import ex.ss.lib.base.extension.viewBinding
import ex.ss.lib.base.fragment.BaseFragment
import ex.ss.lib.tools.extension.collectWithOwner

class PayFragment : BaseFragment<FragmentPayBinding>() {

    override val binding: FragmentPayBinding by viewBinding()
    private val payAdapter by lazy { PayAdapter() }

    override fun initView() {
        AppViewModel.requireCommChannelConfig {config->
            val text = StringBuilder().apply {
                repeat(3) { append(config.payment_announcement).append("  ") }
            }.toString()
            binding.tvPayHint.text = text
        }
        binding.includeTitleBar.tvBack.setOnClickListener { requireActivity().finish() }
        binding.includeTitleBar.tvBack.text = "订阅"
        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvList.adapter = payAdapter
        payAdapter.setOnItemClick { data, pos ->
            StartPayDialog().apply {
                updatePlan(data)
                onStartPay { startPayItem, couponData, order ->
                    showPayMethod(data, startPayItem, couponData, order)
                }
            }.show(childFragmentManager, "StartPayDialog")
        }
    }

    private fun showPayMethod(
        plan: Plan,
        startPayItem: PlanStartPayItem,
        couponData: CouponData?,
        order: String,
    ) {
        PayMethodDialog().apply {
            updateInfo(
                planName = plan.name,
                payPrice = startPayItem.price,
                couponPrice = couponData?.discountPrice(startPayItem.price) ?: 0,
                couponShow = couponData?.show() ?: "0",
                order = order
            )
        }.show(childFragmentManager, "PayMethodDialog")
    }

    override fun initData() {
        showLoading()
        UserViewModel.planFetch().collectOwner(viewLifecycleOwner) {
            dismissLoading()
            onSuccess {
                payAdapter.submitList(it)
            }
        }
    }

    override fun initialize() {

    }
}