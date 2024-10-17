package com.proxy.base.config

import com.google.gson.Gson
import com.proxy.base.data.LoginData
import com.proxy.base.data.SubscribeInfo
import com.proxy.base.data.UserInfo
import com.proxy.base.proxy.VPNProxy
import ex.ss.lib.components.mmkv.IMMKVDelegate
import ex.ss.lib.components.mmkv.kvDelegate

object UserConfig : IMMKVDelegate() {
    override fun mmkvName(): String = "appConfig"

    private const val LOGIN_TYPE_GUEST = 0 //设备登录（游客）
    private const val LOGIN_TYPE_ACCOUNT = 1 // 账户登录（正式）

    var paying by kvDelegate(false)

    var token by kvDelegate("")
    var session_id by kvDelegate("")
    var auth_data by kvDelegate("")
    private var userInfo by kvDelegate("")
    private var subscribeInfo by kvDelegate("")
    private var loginType by kvDelegate(LOGIN_TYPE_GUEST)
    private var inviteUserId by kvDelegate("")

    private val gson by lazy { Gson() }

    fun saveLoginData(data: LoginData) {
        AppConfig.isFirstOpen = false
        this.auth_data = data.auth_data
        this.token = data.token
        this.session_id = data.session_id
    }

    fun saveUser(info: UserInfo) {
        userInfo = gson.toJson(info)
        inviteUserId = info.invite_user_id ?: ""
        loginType = if (info.isGuest()) LOGIN_TYPE_GUEST else LOGIN_TYPE_ACCOUNT
    }

    fun logout() {
        VPNProxy.stopTunnel()
        this.auth_data = ""
        this.userInfo = ""
        this.subscribeInfo = ""
    }

    fun isLogin(): Boolean {
        return auth_data.isNotEmpty()
    }

    fun isGuest(): Boolean {
        return loginType == LOGIN_TYPE_GUEST
    }

    fun isAccountLogin(): Boolean {
        return loginType == LOGIN_TYPE_ACCOUNT
    }

    fun isBindInviteUser(): Boolean {
        return inviteUserId.isNotEmpty()
    }

    fun getUser(): UserInfo? {
        return runCatching { gson.fromJson(userInfo, UserInfo::class.java) }.getOrNull()
    }

    fun getSubscribeInfo(): SubscribeInfo? {
        return runCatching { gson.fromJson(subscribeInfo, SubscribeInfo::class.java) }.getOrNull()
    }

    fun saveSubscribeInfo(data: SubscribeInfo) {
        subscribeInfo = gson.toJson(data)
    }
}
