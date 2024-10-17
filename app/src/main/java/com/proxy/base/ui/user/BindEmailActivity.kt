package com.proxy.base.ui.user

import android.os.CountDownTimer
import androidx.lifecycle.lifecycleScope
import com.proxy.base.R
import com.proxy.base.data.BindEmail
import com.proxy.base.databinding.ActivityBindEmailBinding
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.setOnAntiViolenceClickListener
import ex.ss.lib.base.extension.viewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BindEmailActivity : BasicActivity<ActivityBindEmailBinding>() {

    override val binding: ActivityBindEmailBinding by viewBinding()

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
        binding.includeTitleBar.tvBack.text = "绑定邮箱"

        binding.tvBind.setOnAntiViolenceClickListener(3000) {
            bindEmail()
        }
        binding.tvCodeGet.setOnClickListener {
            getSmsCode()
        }
    }

    private fun bindEmail() {
        val email = binding.etInputAccount.text.toString()
        if (email.isEmpty()) {
            showToastDialog("请输入绑定邮箱")
            return
        }
        val code = binding.etInputCode.text.toString()
        if (code.isEmpty()) {
            showToastDialog(R.string.login_code_hint)
            return
        }
        val pwd = binding.etInputPwd.text.toString()
        if (pwd.isEmpty()) {
            showToastDialog("请输入登录密码")
            return
        }
        val body = BindEmail(email, code, pwd)
        lifecycleScope.launch {
            withContext(Dispatchers.Main) { showLoading() }
            val bind = UserViewModel.bindEmail(body)
            withContext(Dispatchers.Main) { dismissLoading() }
            if (bind.isSuccess()) {
                showToastDialog(bind.data).setOnDismissCallback {
                    finish()
                }
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