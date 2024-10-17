package com.proxy.base.ui.invite

import com.proxy.base.data.TransferBody
import com.proxy.base.databinding.DialogTransferBinding
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.dialog.BaseDialog
import ex.ss.lib.base.extension.viewBinding

class TransferDialog : BaseDialog<DialogTransferBinding>() {

    override val binding: DialogTransferBinding by viewBinding()

    private var onTransferCallback: (() -> Unit)? = null

    fun setTransferCallback(callback: () -> Unit) {
        this.onTransferCallback = callback
    }

    override fun initView() {
        binding.tvCancel.setOnClickListener { dismiss() }
        binding.tvDone.setOnClickListener {
            bindInviteCode()
        }
    }

    private fun bindInviteCode() {
        val amount = binding.etInputAmount.text.toString()
        if (amount.isEmpty()) {
            showToastDialog("请输入需要转入的余额")
            return
        }
        val amountCent = runCatching { amount.toFloatOrNull() ?: 0F }.getOrElse { 0F }.let {
            (it * 100F).toInt()
        }
        showLoading()
        UserViewModel.transfer(TransferBody(amountCent))
            .collectOwner(viewLifecycleOwner) {
                dismissLoading()
                onSuccess {
                    showToastDialog("转入成功").setOnDismissCallback {
                        dismiss()
                        onTransferCallback?.invoke()
                    }
                }
            }
    }

    override fun initData() {

    }

    override fun dimAmount(): Float = 0.7F

    override fun isFullWidth(): Boolean = true

    override fun isFullHeight(): Boolean = true
}