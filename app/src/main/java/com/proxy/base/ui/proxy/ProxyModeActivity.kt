package com.proxy.base.ui.proxy

import androidx.recyclerview.widget.LinearLayoutManager
import com.proxy.base.R
import com.proxy.base.config.NodeConfig
import com.proxy.base.databinding.ActivityProxyModeBinding
import com.proxy.base.model.NodeViewModel
import com.proxy.base.model.ProxyModeItem
import com.proxy.base.proxy.ProxyMode
import com.proxy.base.ui.BasicActivity
import ex.ss.lib.base.extension.viewBinding

class ProxyModeActivity : BasicActivity<ActivityProxyModeBinding>() {

    override val binding: ActivityProxyModeBinding by viewBinding()

    private val adapter by lazy { ProxyModeAdapter() }

    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvTitle.text = getString(R.string.mode_title)

        binding.rvNodeList.layoutManager = LinearLayoutManager(this)
        binding.rvNodeList.adapter = adapter
        binding.rvNodeList.itemAnimator = null
        adapter.setOnItemClick { item, position ->
            NodeViewModel.selectMode(item.mode.value, item.name)
            featModeData()
            finish()
        }
    }

    override fun initData() {
        featModeData()
    }

    private fun featModeData() {
        val proxyModeName = NodeConfig.proxyModeName()
        val list = listOf(
            ProxyModeItem(
                "智能模式",
                "数据智能分流到本地或代理，访问出国速度快",
                ProxyMode.Rule,
                R.drawable.rule_mode,
                proxyModeName == "智能模式"
            ),
            ProxyModeItem(
                "全局模式",
                "所有数据都走代理，会影响国内访问速度",
                ProxyMode.Global,
                R.drawable.global_mode,
                proxyModeName == "全局模式"
            ),
            ProxyModeItem(
                "回国模式",
                "数据智能分流到本地或代理，访问回国速度快",
                ProxyMode.Repatriate,
                R.drawable.back_mode,
                proxyModeName == "回国模式"
            ),
        )
        adapter.submitList(list)
    }


}
