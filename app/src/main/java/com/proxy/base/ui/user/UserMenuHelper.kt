package com.proxy.base.ui.user

import androidx.lifecycle.MutableLiveData
import com.proxy.base.R
import com.proxy.base.config.UserConfig
import com.proxy.base.model.UserMenu
import com.proxy.base.model.UserMenuGroup
import ex.ss.lib.tools.extension.formatCent
import java.util.concurrent.atomic.AtomicBoolean

object UserMenuHelper {

    const val MENU_ORDER = 0
    const val MENU_ACCOUNT = 1
    const val MENU_WALLET = 2
    const val MENU_SETTING = 3
    const val MENU_EMAIL = 4
    const val MENU_WEBSITE = 5
    const val MENU_INVITE = 6
    const val MENU_TG = 7
    const val MENU_HELP = 8
    const val MENU_INVITE_CODE = 9
    const val MENU_QUESTION = 10

    val userMenuLiveData = MutableLiveData<List<UserMenuGroup>>()

    fun userMenus() {
        val userMenus = mutableListOf<UserMenuGroup>()

        val topMenu = mutableListOf<UserMenu>().apply {
            add(UserMenu(MENU_ORDER, R.drawable.user_menu_order, "我的订单"))
            if (UserConfig.isAccountLogin()) {
                add(
                    UserMenu(
                        MENU_ACCOUNT,
                        R.drawable.user_menu_account,
                        "我的账户",
                        "已绑定",
                        R.drawable.shape_binded_email_bg
                    )
                )
            } else {
                add(
                    UserMenu(
                        MENU_ACCOUNT,
                        R.drawable.user_menu_account,
                        "我的账户",
                        "绑定邮箱赠送时长",
                        R.drawable.shape_user_menu_desc_bg
                    )
                )
            }
            val balance = "${(UserConfig.getUser()?.balance ?: 0).formatCent()} ¥"
            add(UserMenu(MENU_WALLET, R.drawable.user_menu_wallet, "钱包", value = balance))
            add(UserMenu(MENU_SETTING, R.drawable.user_menu_setting, "设置"))
        }

        userMenus.add(UserMenuGroup(topMenu))

        val bottomMenu = mutableListOf<UserMenu>().apply {
            add(UserMenu(MENU_WEBSITE, R.drawable.user_menu_website, "官方网址"))
            add(UserMenu(MENU_INVITE, R.drawable.user_menu_invite, "邀请赚佣金"))
            add(UserMenu(MENU_TG, R.drawable.user_menu_tg, "加入TG群组"))
            add(UserMenu(MENU_HELP, R.drawable.user_menu_helper, "帮助中心"))
            if (!UserConfig.isBindInviteUser()) {
                add(UserMenu(MENU_INVITE_CODE, R.drawable.user_menu_invite_code, "绑定推荐码"))
            }
            add(UserMenu(MENU_QUESTION, R.drawable.user_menu_question, "我的工单"))
        }
        userMenus.add(UserMenuGroup(bottomMenu))

        userMenuLiveData.postValue(userMenus)
    }


}