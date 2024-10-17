package com.proxy.base.ui.user

import android.os.CountDownTimer
import com.proxy.base.R
import com.proxy.base.data.ForgetPwdBody
import com.proxy.base.data.ResetPwdBody
import com.proxy.base.databinding.ActivityFindPwdBinding
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.toast
import ex.ss.lib.base.extension.viewBinding

class FindPwdActivity : BasicActivity<ActivityFindPwdBinding>() {

    override val binding: ActivityFindPwdBinding by viewBinding()

    private val countDownload by lazy {
        object : CountDownTimer(60_000, 500) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvCodeGet.isEnabled = false
                binding.tvCodeGet.text = "${millisUntilFinished / 1000} s"
            }

            override fun onFinish() {
                binding.tvCodeGet.isEnabled = true
                binding.tvCodeGet.text = getString(R.string.login_code_get)
            }
        }
    }

    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvBack.text = "忘记密码"

        binding.tvFind.setOnClickListener {
            resetPwd()
        }
        binding.tvCodeGet.setOnClickListener {
            getSmsCode()
        }
    }

    private fun getSmsCode() {
        val email = binding.etInputAccount.text.toString()
        if (email.isEmpty()) {
            showToastDialog(R.string.login_email_hint)
            return
        }
        binding.tvCodeGet.isEnabled = false
        showLoading()
        UserViewModel.getSmsCodeForRegister(email).collectOwner(this) {
            dismissLoading()
            onSuccess {
                showToastDialog(getString(R.string.get_sms_code_success))
                countDownload.start()
            }
            onFailed {
                binding.tvCodeGet.isEnabled = true
            }
        }
    }

    private fun resetPwd() {
        val account = binding.etInputAccount.text.toString()
        if (account.isEmpty()) {
            showToastDialog("请输入邮箱")
            return
        }
        val code = binding.etInputCode.text.toString()
        if (code.isEmpty()) {
            showToastDialog("请输入验证码")
            return
        }
        val newPwd = binding.etInputPwd.text.toString()
        if (newPwd.isEmpty()) {
            showToastDialog("请确认新密码")
            return
        }
        showLoading()
        UserViewModel.forgetPwd(ForgetPwdBody(account, newPwd, code)).collectOwner(this) {
            dismissLoading()
            onSuccess {
                showToastDialog(success.msg)
                finish()
            }
        }
    }

    override fun initData() {
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownload.cancel()
    }
}