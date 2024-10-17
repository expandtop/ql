package com.proxy.base.ui.login

import android.content.Intent
import android.os.CountDownTimer
import com.proxy.base.R
import com.proxy.base.data.RegisterBody
import com.proxy.base.databinding.ActivityRegisterBinding
import com.proxy.base.model.AppViewModel
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.ui.main.MainActivity
import com.proxy.base.ui.message.HelpDetailActivity
import com.proxy.base.ui.service.AppServer
import com.proxy.base.util.collectOwner
import com.proxy.base.util.passwordStyle
import ex.ss.lib.base.extension.setOnAntiViolenceClickListener
import ex.ss.lib.base.extension.toast
import ex.ss.lib.base.extension.viewBinding

class RegisterActivity : BasicActivity<ActivityRegisterBinding>() {

    override val binding: ActivityRegisterBinding by viewBinding()

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
        binding.etInputPwd.passwordStyle(binding.ivPwdEye)
        binding.etInputConfirmPwd.passwordStyle(binding.ivConfirmPwdEye)

        binding.ivRegisterClose.setOnClickListener { finish() }
        binding.tvRegister.setOnAntiViolenceClickListener(3000) {
            register()
        }
        binding.tvCodeGet.setOnClickListener {
            getSmsCode()
        }
        binding.ivRegisterService.setOnClickListener {
            AppServer.open(this)
        }
        binding.tvRegisterProto.setOnClickListener {
            AppViewModel.aboutusTos().collectOwner(this) {
                onSuccess {
                    data.firstOrNull()?.also {
                        startActivity(HelpDetailActivity::class.java) {
                            putExtra("TITLE", it.title)
                            putExtra("CONTENT", it.content)
                        }
                    }
                }
            }
        }
    }

    private fun register() {
        val email = binding.etInputAccount.text.toString()
        if (email.isEmpty()) {
            showToastDialog(R.string.login_account_hint)
            return
        }
        val emailCode = binding.etInputCode.text.toString()
        if (emailCode.isEmpty()) {
            showToastDialog(R.string.login_code_hint)
            return
        }
        val pass = binding.etInputPwd.text.toString()
        if (pass.isEmpty()) {
            showToastDialog(R.string.login_pwd_hint)
            return
        }
        val confirmPwd = binding.etInputConfirmPwd.text.toString()
        if (confirmPwd.isEmpty()) {
            showToastDialog(R.string.login_pwd_hint)
            return
        }
        if (confirmPwd != pass) {
            showToastDialog("两次输入的密码不相同")
            return
        }
        val inviteCode = binding.etInputInviteCode.text.toString()
        showLoading()
        val body = RegisterBody(email, pass, emailCode, inviteCode)
        UserViewModel.register(body).collectOwner(this) {
            dismissLoading()
            onSuccess {
                showToastDialog(getString(R.string.register_success))
                startActivity(MainActivity::class.java) {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                finish()
            }
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

    override fun initData() {
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownload.cancel()
    }
}