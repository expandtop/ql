package com.proxy.base.ui.user

import android.content.Intent
import android.util.Log
import androidx.lifecycle.asFlow
import androidx.recyclerview.widget.LinearLayoutManager
import com.proxy.base.R
import com.proxy.base.databinding.FragmentUserBinding
import com.proxy.base.model.AppViewModel
import com.proxy.base.model.NodeViewModel
import com.proxy.base.model.UserViewModel
import com.proxy.base.ui.about.SettingActivity
import com.proxy.base.ui.checkLogin
import com.proxy.base.ui.invite.InviteActivity
import com.proxy.base.ui.invite.InviteBindCodeDialog
import com.proxy.base.ui.login.LoginActivity
import com.proxy.base.ui.message.HelpActivity
import com.proxy.base.ui.pay.OrderActivity
import com.proxy.base.ui.pay.PayActivity
import com.proxy.base.ui.ticket.TicketsActivity
import com.proxy.base.util.BrowserUtils
import com.proxy.base.util.collectOwner
import com.proxy.base.util.secondExpire
import ex.ss.lib.base.extension.setOnAntiViolenceClickListener
import ex.ss.lib.base.extension.viewBinding
import ex.ss.lib.base.fragment.BaseFragment
import ex.ss.lib.tools.extension.ByteUnit
import ex.ss.lib.tools.extension.TimeUnit
import ex.ss.lib.tools.extension.formatByteUnit
import ex.ss.lib.tools.extension.formatSecondDate
import ex.ss.lib.tools.extension.formatTime
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull

class UserFragment : BaseFragment<FragmentUserBinding>() {

    override val binding: FragmentUserBinding by viewBinding()

    private val adapter by lazy { UserMenuAdapter() }

    override fun initView() { //初始化UI
        binding.mineTitleBar.tvBack.setOnClickListener { requireActivity().finish() }
        binding.cardPay.setOnAntiViolenceClickListener {
            checkLogin {
                startActivity(Intent(requireActivity(), PayActivity::class.java))
            }
        }
    }

    override fun initData() {
        initMenu()
        initUserInfo()
    }

    override fun onResume() {
        super.onResume()
        UserViewModel.refreshUserInfo()
        NodeViewModel.featOnlySubInfo()
    }

    private fun initMenu() {
        binding.rvMenu.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMenu.itemAnimator = null
        binding.rvMenu.adapter = adapter
        UserMenuHelper.userMenuLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        UserMenuHelper.userMenus()
        adapter.setOnMenuClick {
            when (it.type) {
                UserMenuHelper.MENU_ORDER -> {
                    startActivity(Intent(requireContext(), OrderActivity::class.java))
                }

                UserMenuHelper.MENU_ACCOUNT -> {
                    startActivity(Intent(requireContext(), AccountActivity::class.java))
                }

                UserMenuHelper.MENU_SETTING -> {
                    startActivity(Intent(requireContext(), SettingActivity::class.java))
                }

                UserMenuHelper.MENU_WEBSITE -> {
                    AppViewModel.requireCommChannelConfig {
                        BrowserUtils.openBrowser(requireActivity(), it.official_website_address)
                    }
                }

                UserMenuHelper.MENU_INVITE -> {
                    startActivity(Intent(requireContext(), InviteActivity::class.java))
                }

                UserMenuHelper.MENU_TG -> {
                    AppViewModel.requireCommChannelConfig {
                        BrowserUtils.openBrowser(requireActivity(), it.telegram_discuss_link)
                    }
                }

                UserMenuHelper.MENU_HELP -> {
                    startActivity(Intent(requireContext(), HelpActivity::class.java))
                }

                UserMenuHelper.MENU_INVITE_CODE -> {
                    InviteBindCodeDialog().show(childFragmentManager, "InviteBindCodeDialog")
                }

                UserMenuHelper.MENU_QUESTION -> {
                    startActivity(Intent(requireContext(), TicketsActivity::class.java))
                }
            }
        }
    }

    private fun initUserInfo() {
        val subscribeInfoFlow = NodeViewModel.subscribeInfoLiveData.asFlow().mapNotNull { it }
        val userInfoFlow = UserViewModel.userInfoLiveData.asFlow().mapNotNull { it }
        subscribeInfoFlow.combine(userInfoFlow) { subInfo, userInfo -> subInfo to userInfo }
            .collectOwner(viewLifecycleOwner) {
                val isExpire = first.isExpire()
                binding.ivBg.setBackgroundResource(if (isExpire) R.drawable.mine_top_drak_bg else R.drawable.mine_top_light_bg)
                binding.tvLevel.text = when {
                    second.isGuest() -> "登录/注册"
                    else -> first.plan.name + if (isExpire) "(过期)" else ""
                }

                binding.tvUserAvailableTime.text = when {
                    second.isGuest() -> "有效期：${first.plan.name}"
                    first.expired_at == null -> "有效期：长期有效"
                    else -> {
                        val time =
                            maxOf(0L, (first.subExpireTime() * 1000L - System.currentTimeMillis()))
                        "有效期：${time.formatTime(TimeUnit.DAY)}天" + if (isExpire) "（过期）" else ""
                    }
                }

                binding.tvUserExpire.text = when {
                    first.expired_at == null -> "到期时间：长期有效"
                    else -> "到期时间：${first.subExpireTime().formatSecondDate("yyyy-MM-dd HH:mm")}"
                }

                val used = first.u + first.d
                binding.tvUsedData.text =
                    "${used.formatByteUnit(ByteUnit.GB)} / ${first.enableTransfer()}"
                binding.pbUsed.max = 100
                val progress = used * 1F / first.transfer_enable * 100
                binding.pbUsed.progress = progress.toInt()



                binding.mineTitleBar.tvTitle.text = when {
                    second.isGuest() -> " 游客ID: ${second.id}"
                    else -> second.email
                }

                if (second.isGuest()) {
                    binding.layoutInfo.setOnClickListener {
                        startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
                            putExtra("SHOW_GUEST_TO", false)
                        })
                    }
                } else {
                    binding.layoutInfo.setOnClickListener(null)
                }
                UserMenuHelper.userMenus()
            }
    }

    override fun initialize() {

    }
}