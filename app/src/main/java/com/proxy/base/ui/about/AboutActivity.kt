package com.proxy.base.ui.about

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proxy.base.BuildConfig
import com.proxy.base.R
import com.proxy.base.config.AppConfig
import com.proxy.base.databinding.ActivityAboutBinding
import com.proxy.base.func.NetworkLog
import com.proxy.base.model.AppViewModel
import com.proxy.base.model.UserMenu
import com.proxy.base.model.UserMenuGroup
import com.proxy.base.ui.BasicActivity
import com.proxy.base.ui.dialog.showToastDialog
import com.proxy.base.ui.message.HelpDetailActivity
import com.proxy.base.ui.service.AppServer
import com.proxy.base.ui.user.UserMenuAdapter
import com.proxy.base.util.collectOwner
import ex.ss.lib.base.extension.viewBinding
import ex.ss.lib.tools.common.CopyTools
import ex.ss.lib.tools.common.SpannableTools
import ex.ss.lib.tools.extension.formatByte
import ex.ss.lib.tools.extension.formatByteUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.atomic.AtomicLong

/**
 * 2024/7/24
 */
class AboutActivity : BasicActivity<ActivityAboutBinding>() {

    override val binding: ActivityAboutBinding by viewBinding()
    private val menusLiveData = MutableLiveData<List<UserMenu>>()

    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvBack.text = getString(R.string.user_menu_about)
        binding.tvVersion.text = "当前版本：${BuildConfig.VERSION_NAME}"
        binding.tvInfo.text =
            SpannableTools.Builder().text("android_${AppConfig.channel}_${AppConfig.deviceId}")
                .size(10, true).color(Color.WHITE).text("\n青龙加速器 版权所有").size(12, true)
                .color(Color.GRAY).build()

        val onMenuClick: (UserMenu) -> Unit = {
            when (it.type) {
                0 -> {
                    lifecycleScope.launch {
                        val logs = NetworkLog.getLogs()
                        if (logs.isNotEmpty()) {
                            CopyTools.copy(this@AboutActivity, logs, "导出网络日志")
                            showToastDialog("网络日志已复制到粘贴板")
                        }
                    }
                }

                1 -> {
                    AppServer.open(this)
                }

                2 -> {
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

                3 -> {
                    lifecycleScope.launch {
                        cacheDir.deleteRecursively().apply {
                            if (this) showToastDialog("缓存已清理")
                        }
                        buildMenu()
                    }
                }
            }
        }
        menusLiveData.observe(this) {
            initMenu(binding.rvAboutMenu, it, onMenuClick)
        }
        buildMenu()
    }

    private fun buildMenu() = lifecycleScope.launch {
        val menus = mutableListOf<UserMenu>().apply {
            add(UserMenu(0, 0, "导出网络日志"))
            add(UserMenu(1, 0, "联系我们"))
            add(UserMenu(2, 0, "使用条款"))
            add(UserMenu(3, 0, "清理缓存", value = getCacheSize()))
        }
        menusLiveData.postValue(menus)
    }

    private suspend fun getCacheSize(): String = withContext(Dispatchers.IO) {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val size = AtomicLong(0)
        getDirSize(cacheDir, size)
        size.get().let {
            if (it <= 0) "0.00KB" else it.formatByteUnit()
        }
    }

    private fun getDirSize(dir: File, size: AtomicLong) {
        val files = dir.listFiles()
        for (file in files) {
            if (file.isDirectory) {
                getDirSize(file, size)
            } else {
                size.addAndGet(file.length())
            }
        }
    }

    private fun initMenu(
        recyclerView: RecyclerView, menus: List<UserMenu>, onMenuClick: (UserMenu) -> Unit,
    ) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = UserMenuAdapter().apply {
            submitList(listOf(UserMenuGroup(menus)))
            setOnMenuClick(onMenuClick)
        }
    }

    override fun initData() {
    }
}