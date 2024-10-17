package com.proxy.base.ui.user

import com.proxy.base.data.ResetPwdBody
import com.proxy.base.databinding.ActivityResetPwdBinding
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.util.collectOwner
import com.proxy.base.util.passwordStyle
import ex.ss.lib.base.extension.viewBinding

class ResetPwdActivity : BasicActivity<ActivityResetPwdBinding>() {

    override val binding: ActivityResetPwdBinding by viewBinding()

    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvBack.text = "修改密码"

        binding.tvFindPwd.setOnClickListener {
            startActivity(FindPwdActivity::class.java)
        }

        binding.etInputOldPwd.passwordStyle(binding.ivOldPwdEye, false)
        binding.etInputNewPwd.passwordStyle(binding.ivNewPwdEye, false)
        binding.etInputNewPwdConfirm.passwordStyle(binding.ivNewPwdConfirmEye, false)
        binding.tvReset.setOnClickListener {
            resetPwd()
        }
    }

    private fun resetPwd() {
        val oldPwd = binding.etInputOldPwd.text.toString()
        if (oldPwd.isEmpty()) {
            showToastDialog("请输入旧密码")
            return
        }
        val newPwd = binding.etInputNewPwd.text.toString()
        if (newPwd.isEmpty()) {
            showToastDialog("请输入新密码")
            return
        }
        val newPwdConfirm = binding.etInputNewPwdConfirm.text.toString()
        if (newPwdConfirm.isEmpty()) {
            showToastDialog("请确认新密码")
            return
        }
        if (newPwd != newPwdConfirm) {
            showToastDialog("两次输入的密码不相同")
            return
        }
        showLoading()
        UserViewModel.resetPwd(ResetPwdBody(oldPwd, newPwd)).collectOwner(this) {
            dismissLoading()
            onSuccess {
                showToastDialog(success.msg)
                finish()
            }
        }
    }

    override fun initData() {
    }
}