package com.proxy.base.ui.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.net.VpnService
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jfdream.xvpn.vpn.Callback
import com.proxy.base.R
import com.proxy.base.config.NodeConfig
import com.proxy.base.config.UserConfig
import com.proxy.base.data.OrderSaveBody
import com.proxy.base.databinding.FragmentHomeBinding
import com.proxy.base.model.AppViewModel
import com.proxy.base.model.NodeViewModel
import com.proxy.base.model.UserViewModel
import com.proxy.base.proxy.VPNProxy
import com.proxy.base.ui.checkLogin
import com.proxy.base.ui.dialog.showAd
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.ui.invite.InviteActivity
import com.proxy.base.ui.main.WebsiteActivity
import com.proxy.base.ui.message.MessageActivity
import com.proxy.base.ui.pay.PayActivity
import com.proxy.base.ui.pay.PayMethodDialog
import com.proxy.base.ui.proxy.ModeDialog
import com.proxy.base.ui.proxy.NodeListActivity
import com.proxy.base.ui.service.AppServer
import com.proxy.base.ui.user.MineActivity
import com.proxy.base.util.collectOwner
import com.proxy.base.util.secondExpire
import ex.ss.lib.base.extension.setOnAntiViolenceClickListener
import ex.ss.lib.base.extension.toast
import ex.ss.lib.base.extension.viewBinding
import ex.ss.lib.base.fragment.BaseFragment
import ex.ss.lib.components.result.launchForResult
import ex.ss.lib.tools.common.SpannableTools
import ex.ss.lib.tools.extension.TimeUnit
import ex.ss.lib.tools.extension.formatTimeAndLess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.max


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val binding: FragmentHomeBinding by viewBinding()

    private var expireJob: Job? = null
    private val navCateAdapter by lazy { NavCateAdapter() }
    private val isClickToggle = AtomicBoolean(false)

    override fun initView() {
        binding.ivService.setOnAntiViolenceClickListener {
            checkLogin {
                AppServer.open(requireActivity())
            }
        }
        binding.layoutNode.setOnAntiViolenceClickListener {
            checkLogin {
                startActivity(Intent(requireContext(), NodeListActivity::class.java))
            }
        }
        binding.layoutMode.setOnClickListener {
            ModeDialog().show(childFragmentManager, "ModeDialog")
        }
        binding.ivShare.setOnClickListener {
            startActivity(Intent(requireContext(), InviteActivity::class.java))
        }
        binding.ivMessage.setOnClickListener {
            startActivity(Intent(requireContext(), MessageActivity::class.java))
        }
        binding.ivMine.setOnClickListener {
            startActivity(Intent(requireContext(), MineActivity::class.java))
        }
        initDrawLayout()
    }

    private fun initDrawLayout() {
        binding.layoutDrawContent.setOnClickListener {}
        binding.tvWebsite.setOnClickListener {
            binding.root.openDrawer(GravityCompat.END)
        }
        binding.ivDrawClose.setOnClickListener {
            binding.root.closeDrawer(GravityCompat.END)
        }
        binding.rvDrawList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDrawList.adapter = navCateAdapter
        AppViewModel.navCate().collectOwner(viewLifecycleOwner) {
            onSuccess {
                navCateAdapter.submitList(it)
            }
        }
        navCateAdapter.setOnItemClick { data, pos ->
            startActivity(Intent(requireActivity(), WebsiteActivity::class.java).apply {
                putExtra("ID", data.id)
                putExtra("TITLE", data.name)
            })
        }
    }

    override fun initData() {
        initXrayVPN()
        initProxyNode()
        initProxyMode()
        initUserInfo()
        initSign()
        initHomeAd()
    }

    private fun initSign() {
        UserViewModel.checkSign().collectOwner(viewLifecycleOwner) {
            onSuccess {
                updateSignState(it)
            }
        }
    }

    private fun initHomeAd() {
        AppViewModel.mainAd().collectOwner(viewLifecycleOwner) {
            onSuccess {
                Log.d("MAIN_AD","onSuccess")
                requireActivity().showAd(it ?: listOf())
            }
        }
    }

    private fun updateSignState(signed: Boolean) {
        binding.layoutSing.isInvisible = false
        binding.tvSign.text = if (signed) "已签到" else "签到—送时长"
        binding.layoutSing.setCardBackgroundColor(
            if (signed) Color.parseColor("#FF19C179") else Color.parseColor("#FFFFC737")
        )
        binding.layoutSing.setOnClickListener {
            if (!signed) {
                UserViewModel.sign().collectOwner(viewLifecycleOwner) {
                    onSuccess {
                        updateSignState(true)
                        showToastDialog(data)
                    }
                }
            } else {
                showToastDialog("已签到")
            }
        }
    }

    private fun initUserInfo() {
        NodeViewModel.subscribeInfoLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                val used = it.usedTransfer()
                binding.tvUsedValue.text = SpannableTools.Builder().text(used.first).size(36, true)
                    .color(if (it.isOverTransfer()) "#FFFF6864" else "#FF19C179")
                    .text("${used.second}/${it.enableTransfer()}").size(14, true).color("#7FFFFFFF")
                    .build()
                when {
                    it.expired_at == null -> {
                        expireJob?.cancel()
                        binding.tvExpireValue.text = "长期有效"
                    }
                    it.subExpireTime().secondExpire() -> {
                        expireJob?.cancel()
                        binding.tvExpireValue.text = "套餐过期"
                    }
                    else -> showExpireCountDown(it.subExpireTime())
                }
                if (it.isOverTransfer()) {
                    VPNProxy.stopTunnel()
                    binding.tvConnect.setBackgroundResource(R.drawable.selector_vpn_toggle_reset)
                    binding.tvConnect.text = "重置流量"
                } else {
                    val currentStatus = VPNProxy.currentStatus()
                    setConnectStatusView(currentStatus to false)
                }
            }
        }
    }

    private fun showExpireCountDown(expireAtSecond: Int) {
        val expireTime = AtomicLong(max(0L, expireAtSecond * 1000L - System.currentTimeMillis()))
        expireJob?.cancel()
        expireJob = viewLifecycleOwner.lifecycleScope.launch {
            while (expireTime.get() >= 0) {
                val current = expireTime.get()
                val dayTime = current.formatTimeAndLess(TimeUnit.DAY)
                val hourTime = dayTime.second.formatTimeAndLess(TimeUnit.HOUR)
                val minuteTime = hourTime.second.formatTimeAndLess(TimeUnit.MINUTE)
                val secondTime = minuteTime.second.formatTimeAndLess(TimeUnit.SECOND)
                withContext(Dispatchers.Main) {
                    binding.tvExpireValue.text =
                        SpannableTools.Builder().text("${dayTime.first}").size(36, true)
                            .color("#FF19C179").text("天").size(14, true).color("#7FFFFFFF")
                            .text("${hourTime.first}").size(36, true).color("#FF19C179").text("时")
                            .size(14, true).color("#7FFFFFFF").text("${minuteTime.first}")
                            .size(36, true).color("#FF19C179").text("分").size(14, true)
                            .color("#7FFFFFFF").text("${secondTime.first}").size(36, true)
                            .color("#FF19C179").text("秒").size(14, true).color("#7FFFFFFF").build()
                }
                delay(1000)
                expireTime.getAndAdd(-1000L)
            }
            binding.tvExpireValue.text = "套餐过期"
        }
    }

    override fun initialize() {
        NodeViewModel.featCacheData()
    }


    private fun initProxyNode() {
        binding.tvCurrentNode.text = getString(R.string.no_node)
        NodeViewModel.currentNodeLiveData.observe(this) {
            binding.layoutNode.isVisible = true
            if (it == null) {
                binding.tvCurrentNode.text = getString(R.string.no_node)
                VPNProxy.stopTunnel()
            } else {
                binding.tvCurrentNode.text = it.name
            }
        }
    }

    private fun initProxyMode() {
        NodeViewModel.proxyModeLiveData.observe(this) {
            binding.layoutMode.isVisible = true
            binding.tvModeValue.text = it.second
        }
    }

    private fun initXrayVPN() {
        VPNProxy.vpnStatus.observe(viewLifecycleOwner) {
            setConnectStatusView(it)
        }

        binding.tvConnect.setOnClickListener {
            checkLogin {
                if (UserConfig.getUser()?.isExpire() == true) {
                    showToastDialog("套餐过期，去续费").setOnDismissCallback {
                        startActivity(Intent(requireContext(), PayActivity::class.java))
                    }
                    return@checkLogin
                }
                if (UserConfig.getSubscribeInfo()?.isOverTransfer() == true) {
                    resetTransfer()
                    return@checkLogin
                }
                isClickToggle.set(true)
                when (VPNProxy.currentStatus()) {
                    Callback.K_Connected -> {
                        VPNProxy.stopTunnel()
                        VPNProxy.stopListenerStatistics()

                    }

                    Callback.K_Connecting -> {

                    }

                    else -> {
                        startXray()
                        VPNProxy.listenerStatistics()
                    }
                }
            }
        }
    }

    private fun resetTransfer(): Boolean {
        val planId = UserConfig.getUser()?.plan_id ?: return false
        val resetPlanPriceFlow = UserViewModel.planFetch().map {
            it.data.firstOrNull { item -> item.id == planId }?.reset_price
        }
        val resetOrderFlow = UserViewModel.orderSave(OrderSaveBody(planId, null, "reset_price"))
            .map { if (it.isSuccess()) it.data else null }
        resetPlanPriceFlow.combine(resetOrderFlow) { price, order -> price to order }
            .collectOwner(viewLifecycleOwner) {
                val price = first
                val order = second
                if (price != null && !order.isNullOrEmpty()) {
                    showPayMethod(price, order)
                }
            }
        return true
    }

    private fun showPayMethod(price: Int, order: String) {
        PayMethodDialog().apply {
            updateInfo(
                planName = "重置流量",
                payPrice = price,
                couponPrice = 0,
                couponShow = "0",
                order = order
            )
        }.show(childFragmentManager, "PayMethodDialog")
    }

    private fun setConnectStatusView(it: Pair<Int, Boolean>) {
//        if (UserConfig.getSubscribeInfo()?.isOverTransfer() == true) {
//            binding.tvConnect.setBackgroundResource(R.drawable.selector_vpn_toggle_reset)
//            binding.tvConnect.text = "重置流量"
//        } else {
            when (it.first) {
                Callback.K_Connected -> {
                    binding.tvConnect.setBackgroundResource(R.drawable.selector_vpn_toggle)
                    binding.tvConnect.isSelected = true
                    binding.tvConnect.text = "断开"
                    if (it.second) showToastDialog("连接成功")
                }

                Callback.K_Disconnected -> {
                    binding.tvConnect.setBackgroundResource(R.drawable.selector_vpn_toggle)
                    binding.tvConnect.isSelected = false
                    binding.tvConnect.text = "连接"
                    if (it.second) showToastDialog("断开连接")
                }

                else -> {}
            }
//        }
    }

    override fun onResume() {
        super.onResume()
        val currentStatus = VPNProxy.currentStatus()
        setConnectStatusView(currentStatus to false)
        NodeViewModel.featOnlySubInfo()
        UserViewModel.refreshUserInfo()
    }

    private fun startXray() = viewLifecycleOwner.lifecycleScope.launch {
        val intent = VpnService.prepare(requireContext())
        if (intent == null) { // 已经获取到 VPN 权限，直接开始
            startTunnel()
        } else {
            val result = requireActivity().launchForResult(
                ActivityResultContracts.StartActivityForResult(), intent
            )
            if (result.resultCode == RESULT_OK) {
                startTunnel()
            } else {
                showToastDialog(getString(R.string.get_vpn_permission_failed))
            }
        }
    }

    private fun startTunnel() {
        val proxyNode = NodeConfig.selectedProxyNode()
        if (proxyNode == null) {
            showToastDialog(getString(R.string.no_node))
            return
        }
        VPNProxy.startTunnel(proxyNode.url)
    }

}