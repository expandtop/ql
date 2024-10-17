package com.proxy.base.model

import com.proxy.base.proxy.ProxyMode
import ex.ss.lib.base.adapter.data.BaseItem

data class UserMenuGroup(val menus: List<UserMenu>) : BaseItem

data class UserMenu(
    val type: Int,
    val icon: Int,
    val title: String,
    val desc: String = "",
    val descBg: Int = 0,
    val value: String = "",
) : BaseItem {

    override fun <T : BaseItem> areContentsTheSame(other: T): Boolean {
        if (other !is UserMenu) return false
        if (other.type != type) return false
        if (other.icon != icon) return false
        if (other.title != title) return false
        if (other.desc != desc) return false
        if (other.descBg != descBg) return false
        if (other.value != value) return false
        return true
    }
}

data class ProxyModeItem(
    val name: String,
    val desc: String,
    val mode: ProxyMode,
    val drawable: Int,
    val isSelect: Boolean
) : BaseItem


