package com.proxy.base.ui.pay

import androidx.recyclerview.widget.LinearLayoutManager
import com.proxy.base.config.UserConfig
import com.proxy.base.data.OrderCheckoutBody
import com.proxy.base.data.PaymentMethod
import com.proxy.base.data.WithDrawBody
import com.proxy.base.databinding.DialogWithdrawMethodBinding
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.dialog.BasicDialog
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.setOnAntiViolenceClickListener
import ex.ss.lib.base.extension.viewBinding
import ex.ss.lib.tools.common.BrowserTools
import ex.ss.lib.tools.extension.formatCent
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class WithDrawMethodDialog : BasicDialog<DialogWithdrawMethodBinding>() {

    private val adapter by lazy { PayMethodAdapter() }

    override val binding: DialogWithdrawMethodBinding by viewBinding()


    private val currentWithDrawMethod = AtomicReference<String>(null)

    override fun initView() {
        binding.root.setOnClickListener { dismiss() }
        binding.layoutContent.setOnClickListener { }
        binding.tvWithDraw.setOnAntiViolenceClickListener {
            startWithDraw()
        }
    }

    private fun startWithDraw() {
        val method = currentWithDrawMethod.get()
        showLoading()
        UserViewModel.withdraw(WithDrawBody(method, UserConfig.getUser()?.email?:"")).collectOwner(this) {
            dismissLoading()
            onSuccess {
                dismiss()
            }
            onFailed {
                dismiss()
            }
        }
//        UserViewModel.orderCheckout(OrderCheckoutBody(order, method.id))
//            .collectOwner(viewLifecycleOwner) {
//                dismissLoading()
//                onSuccess {
//                    dismiss()
//                    BrowserTools.openBrowser(requireContext(), it.data)
//                }
//            }

    }

    override fun initData() {
        binding.rvPayMethodList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPayMethodList.adapter = adapter
        binding.rvPayMethodList.itemAnimator = null
        adapter.setOnItemClick { item, position ->
            currentWithDrawMethod.set(item.payment)
            showMethod(adapter.currentList)
        }
        featModeData()
    }

    private fun featModeData() {
        UserViewModel.withdrawConfig().collectOwner(viewLifecycleOwner) {
            onSuccess {
                val list = it.withdraw_methods.map { PaymentMethod(0, "", it, null, null, false) }
                showMethod(list)
            }
        }
    }

    private fun showMethod(it: List<PaymentMethod>) {
        val list = it.mapIndexed { index, method ->
            if (currentWithDrawMethod.get() == null) {
                method.copy(isSelect = index == 0)
            } else {
                method.copy(isSelect = currentWithDrawMethod.get() == method.payment)
            }
        }.also {
            it.firstOrNull { item -> item.isSelect }?.also { item ->
                currentWithDrawMethod.set(item.payment)
            }
        }
        adapter.submitList(list)
    }

}