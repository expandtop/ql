package com.proxy.base.ui.web

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.proxy.base.databinding.ActivityWebBinding
import ex.ss.lib.base.extension.compoundDrawable
import ex.ss.lib.base.extension.viewBinding

class SimpleWebActivityArgs(
    val title: String,
    val url: String,
    val menu: String = "",
    val menuIcon: Int = 0,
    val menuKey: String = ""
) {

    companion object {

        private const val WEB_ARGS_TITLE = "web_args_title"
        private const val WEB_ARGS_URL = "web_args_url"
        private const val WEB_ARGS_MENU = "web_args_menu"
        private const val WEB_ARGS_MENU_ICON = "web_args_menu_icon"
        private const val WEB_ARGS_MENU_KEY = "web_args_menu_key"
        fun fromBundle(intent: Intent): SimpleWebActivityArgs {
            val bundle = intent.extras
                ?: bundleOf()
            val title = bundle.getString(WEB_ARGS_TITLE, "")
            val url = bundle.getString(WEB_ARGS_URL, "")
            val menu = bundle.getString(WEB_ARGS_MENU, "")
            val menuIcon = bundle.getInt(WEB_ARGS_MENU_ICON, 0)
            val menuKey = bundle.getString(WEB_ARGS_MENU_KEY, "")
            return SimpleWebActivityArgs(title, url, menu, menuIcon, menuKey)
        }
    }

    fun toBundle(): Bundle {
        return bundleOf(
            WEB_ARGS_TITLE to title,
            WEB_ARGS_URL to url,
            WEB_ARGS_MENU to menu,
            WEB_ARGS_MENU_ICON to menuIcon,
            WEB_ARGS_MENU_KEY to menuKey,
        )
    }
}

class SimpleWebActivity : BaseWebActivity() {

    override val binding: ActivityWebBinding by viewBinding()
    private val args by lazy { SimpleWebActivityArgs.fromBundle(intent) }
    companion object {
        private val menuClick = mutableMapOf<String, (String) -> Unit>()
        
        fun registerMenuClick(menuKey: String, onClick: (String) -> Unit) {
            menuClick[menuKey] = onClick
        }
    }

    override fun initView() {
        binding.includeTitleBar.tvBack.setOnClickListener { finish() }
        binding.includeTitleBar.tvTitle.text = args.title
        binding.includeTitleBar.tvMenu.isVisible = false
        if (args.menu.isNotEmpty()) {
            binding.includeTitleBar.tvMenu.isVisible = true
            binding.includeTitleBar.tvMenu.text = args.menu
        }
        if (args.menuIcon != 0) {
            binding.includeTitleBar.tvMenu.isVisible = true
            binding.includeTitleBar.tvMenu.compoundDrawable(left = args.menuIcon)
        }
        if (args.menuKey.isNotEmpty()) {
            binding.includeTitleBar.tvMenu.setOnClickListener {
                menuClick[args.menuKey]?.invoke(args.menuKey)
            }
        }
        super.initView()
    }

    override fun initData() {

    }

    override fun getUrl(): String = args.url
}