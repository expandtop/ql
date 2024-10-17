package com.proxy.base.ui.invite

import com.proxy.base.data.BindInviteCodeBody
import com.proxy.base.databinding.DialogBindInviteCodeBinding
import com.proxy.base.model.AppViewModel
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.dialog.BaseDialog
import ex.ss.lib.base.extension.toast
import ex.ss.lib.base.extension.viewBinding

class InviteBindCodeDialog : BaseDialog<DialogBindInviteCodeBinding>() {

    override val binding: DialogBindInviteCodeBinding by viewBinding()

    override fun initView() {
        AppViewModel.requireCommConfig {
            binding.tvDesc.text = it.binding_instructions
        }
        binding.tvCancel.setOnClickListener { dismiss() }
        binding.tvDone.setOnClickListener {
            bindInviteCode()
        }
    }

    private fun bindInviteCode() {
        val inviteCode = binding.etInputCode.text.toString()
        if (inviteCode.isEmpty()) {
            showToastDialog("请输入邀请码")
            return
        }
        showLoading()
        UserViewModel.inviteBind(BindInviteCodeBody(inviteCode))
            .collectOwner(viewLifecycleOwner) {
                dismissLoading()
                onSuccess {
                    showToastDialog(success.msg)
                    dismiss()
                }
            }
    }

    override fun initData() {

    }

    override fun dimAmount(): Float = 0.7F

    override fun isFullWidth(): Boolean = true

    override fun isFullHeight(): Boolean = true
}