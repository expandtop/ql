package com.proxy.base.ui.login

import android.content.Intent
import androidx.core.view.isVisible
import com.proxy.base.R
import com.proxy.base.data.LoginPwdBody
import com.proxy.base.databinding.ActivityLoginBinding
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.ui.main.MainActivity
import com.proxy.base.ui.service.AppServer
import com.proxy.base.ui.user.FindPwdActivity
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.viewBinding
import ex.ss.lib.tools.extension.isEmail

class LoginActivity : BasicActivity<ActivityLoginBinding>() {

    override val binding: ActivityLoginBinding by viewBinding()

    override fun initView() {
        binding.tvLogin.setOnClickListener {
            login()
        }
        binding.tvRegister.setOnClickListener {
            startActivity(RegisterActivity::class.java)
        }
        binding.tvForgetPwd.setOnClickListener {
            startActivity(FindPwdActivity::class.java)
        }

        binding.ivLoginService.setOnClickListener {
            AppServer.open(this)
        }

        val showGuestTo = intent.getBooleanExtra("SHOW_GUEST_TO", true)
        binding.tvGuestTo.isVisible = showGuestTo
        binding.tvGuestTo.setOnClickListener {
            macLogin()
        }
    }

    private fun goToMain() {
        finish()
        startActivity(MainActivity::class.java) {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    private fun login() {
        val email = binding.etInputAccount.text.toString()
        if (email.isEmpty() || !email.isEmail()) {
            showToastDialog(getString(R.string.login_email_hint))
            return
        }
        val pwd = binding.etInputPwd.text.toString()
        if (pwd.isEmpty() || pwd.length < 8 || pwd.length > 20) {
            showToastDialog(getString(R.string.login_pwd_hint))
            return
        }
        showLoading()
        val body = LoginPwdBody(email, pwd)
        UserViewModel.loginPwd(body).collectOwner(this) {
            dismissLoading()
            onSuccess {
                goToMain()
            }
        }
    }

    private fun macLogin() {
        showLoading()
        UserViewModel.deviceLogin().collectOwner(this) {
            dismissLoading()
            onSuccess {
                goToMain()

            }
        }
    }

    override fun initData() {

    }

}