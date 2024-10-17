package com.proxy.base.ui.invite

import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.RoundedCornersTransformation
import com.proxy.base.R
import com.proxy.base.data.InviteInfo
import com.proxy.base.databinding.ActivityInviteBinding
import com.proxy.base.model.AppViewModel
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.dismissLoading
import com.proxy.base.ui.dialog.showLoading
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.ui.pay.WithDrawMethodDialog
import com.proxy.base.ui.service.AppServer
import com.proxy.base.util.ZXingUtil
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.dp
import ex.ss.lib.base.extension.viewBinding
import ex.ss.lib.tools.common.CopyTools
import ex.ss.lib.tools.extension.formatCent
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference

class InviteActivity : BasicActivity<ActivityInviteBinding>() {

    override val binding: ActivityInviteBinding by viewBinding()

    private val codeLink = AtomicReference("")
    private val code = AtomicReference("")

    private val commissionAdapter by lazy { RebatesCommissionAdapter() }

    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvBack.text = "分享赚佣金"
        binding.includeTitleBar.ivMenu.setImageResource(R.drawable.login_service)
        binding.includeTitleBar.ivMenu.setOnClickListener {
            AppServer.open(this)
        }

        binding.tvCopyLink.setOnClickListener {
            CopyTools.copy(this, codeLink.get(), "invite_link")
            showToastDialog("链接复制成功")
        }

        binding.tvInviteCode.setOnClickListener {
            CopyTools.copy(this, code.get(), "invite_link")
            showToastDialog("邀请码复制成功")
        }

        binding.layoutInviteRecord.setOnClickListener {
            startActivity(InviteRecordActivity::class.java)
        }

        binding.tvConvertAmount.setOnClickListener {
            TransferDialog().apply {
                show(supportFragmentManager, "TransferDialog")
                setTransferCallback {
                    getInviteInfo()
                }
            }
        }

        binding.tvWithdraw.setOnClickListener {
            WithDrawMethodDialog().show(supportFragmentManager, "WithDrawMethodDialog")
        }

        binding.rvInviteGradient.layoutManager = LinearLayoutManager(this)
        binding.rvInviteGradient.adapter = commissionAdapter
    }

    override fun initData() {
        AppViewModel.requireCommConfig {
            binding.tvInviteRule.text = it.Invitation_activity_copywriting
        }
        getInviteInfo()
        UserViewModel.rebates().collectOwner(this) {
            onSuccess {
                commissionAdapter.submitList(this.data.commission)
            }
        }
    }

    private fun getInviteInfo() {
        showLoading()
        UserViewModel.inviteCode().collectOwner(this) {
            dismissLoading()
            first?.also { showInviteInfo(it) }
        }
    }

    // 已注册用户数(未使用)：stat[0]
    //      有效佣金(对应累计获得佣金)：stat[1]
    //      确认中的佣金(对应确认中的佣金)：stat[2]
    //      佣金比例(未使用)：stat[3]
    //      可用佣金(对应我的佣金)：stat[4]
    //      有效邀请人数(对应目前邀请)：stat[5]
    private fun showInviteInfo(info: InviteInfo) {
        binding.tvInviteCode.text = info.getInviteCode()
        binding.tvInviteCommissionValue.text = "${info.stat[2].formatCent()} ¥"
        binding.tvInviteGetCommissionValue.text = "${info.stat[1].formatCent()} ¥"
        binding.tvMyCommission.text = "我的佣金 ${info.stat[4].formatCent()} ¥"
        binding.tvInviteNumber.text = "目前邀请：${info.stat[5]}人"
        codeLink.set(info.invited_link)
        code.set(info.getInviteCode())
        showQrCode(info)
    }

    private fun showQrCode(info: InviteInfo) {
        lifecycleScope.launch {
            val size = this@InviteActivity.resources.displayMetrics.widthPixels - 120.dp
            ZXingUtil.createQRCode(
                this@InviteActivity, info.invited_link, size, size, 0
            ).also {
                if (it != null) {
                    binding.ivQrCode.isVisible = true
                    binding.ivQrCode.load(it) {
                        transformations(RoundedCornersTransformation(8F.dp))
                    }
                } else {
                    binding.ivQrCode.isVisible = false
                }
            }
        }
    }


}