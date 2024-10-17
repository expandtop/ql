package com.proxy.base.model

import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.proxy.base.config.AppConfig
import com.proxy.base.config.UserConfig
import com.proxy.base.data.BindEmail
import com.proxy.base.data.BindInviteCodeBody
import com.proxy.base.data.CouponCheckBody
import com.proxy.base.data.DeviceLoginBody
import com.proxy.base.data.ForgetPwdBody
import com.proxy.base.data.InviteRecord
import com.proxy.base.data.LoginPwdBody
import com.proxy.base.data.OrderCancelBody
import com.proxy.base.data.OrderCheckoutBody
import com.proxy.base.data.OrderSaveBody
import com.proxy.base.data.RegisterBody
import com.proxy.base.data.ResetPwdBody
import com.proxy.base.data.SmsCodeBody
import com.proxy.base.data.TicketClose
import com.proxy.base.data.TicketReply
import com.proxy.base.data.TicketSave
import com.proxy.base.data.TransferBody
import com.proxy.base.data.UserCloseBody
import com.proxy.base.data.UserInfo
import com.proxy.base.data.WithDrawBody
import com.proxy.base.proxy.VPNProxy
import com.proxy.base.repo.Remote
import com.proxy.base.ui.invite.InvitePagingSource
import com.proxy.base.ui.pay.OrderPagingSource
import ex.ss.lib.net.bean.ResponseData
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

object UserViewModel : BaseViewModel() {

    //支付跳转后，回来刷新用户信息
    val payForRefreshUserInfo = AtomicBoolean(false)

    val userInfoLiveData = MutableLiveData<UserInfo?>()

    suspend fun syncDeviceLogin() = Remote.callBase {
        deviceLogin(DeviceLoginBody("android_${AppConfig.channel}_${AppConfig.deviceId}"))
    }.onSuccess {
        UserConfig.saveLoginData(it)
        NodeViewModel.featSubscribe()
        syncUserInfo()
    }

    fun deviceLogin() = apiBase {
        deviceLogin(DeviceLoginBody("android_${AppConfig.channel}_${AppConfig.deviceId}"))
    }.onEach {
        if (it.isSuccess()) {
            UserConfig.saveLoginData(it.data)
            NodeViewModel.featSubscribe()
        }
    }.flatMapConcat {
        if (it.isSuccess()) userInfo() else flowOf(ResponseData.failed(it.failed))
    }

    fun sign() = apiBase { sign() }.onEach {
        if (it.isSuccess()) {
            refreshUserInfo()
            NodeViewModel.featSubscribe()
        }
    }

    fun checkSign() = apiBase { checkSign() }.onEach {
        it.onSuccess {
            refreshUserInfo()
            NodeViewModel.featOnlySubInfo()
        }
    }

    fun refreshUserInfo() = launch {
        if (UserConfig.isLogin()) {
            syncUserInfo()
        }
    }

    suspend fun syncUserInfo() = Remote.callBase { userInfo() }.onSuccess { info ->
        //            LiveChat.initialize(info)
        UserConfig.saveUser(info)
        userInfoLiveData.postValue(info)
        if (info.isExpire()) {
            VPNProxy.stopTunnel()
        }
    }.onFailed {
        UserConfig.getUser()?.also { info ->
            userInfoLiveData.postValue(info)
            if (info.isExpire()) {
                VPNProxy.stopTunnel()
            }
        }
    }

    fun userInfo() = flow {
        emit(syncUserInfo())
    }

    fun getSmsCodeForRegister(email: String) = apiBase { getSmsCode(SmsCodeBody(email)) }

    fun getSmsCodeForReset(email: String) = apiBase { getSmsCode(SmsCodeBody(email)) }

    fun register(register: RegisterBody) = apiBase { register(register) }.onEach {
        if (it.isSuccess()) {
            UserConfig.saveLoginData(it.data)
            NodeViewModel.featSubscribe()
        }
    }.flatMapConcat {
        if (it.isSuccess()) userInfo() else flowOf(ResponseData.failed(it.failed))
    }

    fun resetPwd(body: ResetPwdBody) = apiBase { resetPwd(body) }

    fun forgetPwd(body: ForgetPwdBody) = apiBase { forgetPwd(body) }

    fun loginPwd(body: LoginPwdBody) = apiBase { loginPwd(body) }.onEach {
        if (it.isSuccess()) {
            UserConfig.saveLoginData(it.data)
            NodeViewModel.featSubscribe()
        }
    }.flatMapConcat {
        if (it.isSuccess()) userInfo() else flowOf(ResponseData.failed(it.failed))
    }

    fun loginOut() = removeDevices(UserConfig.session_id).onEach {
        if (it.isSuccess()) {
            UserConfig.logout()
            userInfoLiveData.postValue(null)
        }
    }

    fun loginClose(body: UserCloseBody) = apiBase { loginClose(body) }.onEach {
        if (it.isSuccess()) {
            UserConfig.logout()
            userInfoLiveData.postValue(null)
        }
    }

    fun planFetch() = apiBase { planFetch() }

    fun couponCheck(body: CouponCheckBody) = apiBase { couponCheck(body) }

    fun orderSave(body: OrderSaveBody) = apiBase { orderSave(body) }

    fun getPaymentMethod() = apiBase { getPaymentMethod() }

    fun orderCheckout(body: OrderCheckoutBody) = api { orderCheckout(body) }

    suspend fun bindEmail(body: BindEmail) = Remote.callBase { bindEmail(body) }.onSuccess {
        syncUserInfo()
        NodeViewModel.featOnlySubInfo()
    }

    fun ticketSave(body: TicketSave) = apiBase { ticketSave(body) }
    fun ticketList() = apiBase { ticketFetch() }
    fun ticketDetail(id: String) = apiBase { ticketDetail(id) }
    fun ticketClose(body: TicketClose) = apiBase { ticketClose(body) }
    fun ticketReplay(body: TicketReply) = apiBase { ticketReply(body) }

    fun rebates() = apiBase { rebates() }
    fun withdrawConfig() = apiBase { withdrawConfig() }
    fun withdraw(body: WithDrawBody) = apiBase { withdraw(body) }
    fun transfer(body: TransferBody) = apiBase { transfer(body) }

    val orderPaging by lazy {
        Pager(config = PagingConfig(10), pagingSourceFactory = {
            OrderPagingSource()
        }).flow
    }

    fun orderCancel(body: OrderCancelBody) = apiBase { orderCancel(body) }

    fun inviteBind(body: BindInviteCodeBody) = apiBase { inviteBind(body) }.onEach {
        if (it.isSuccess()) refreshUserInfo()
    }

    fun inviteCode() = flow {
        Remote.callBase { inviteFetch() }.also {
            if (it.isSuccess() && !it.data.getInviteCode().isNullOrEmpty()) {
                emit(it.data to null)
            } else {
                Remote.callBase { inviteSave() }.also {
                    if (it.isSuccess()) {
                        Remote.callBase { inviteFetch() }.also {
                            if (it.isSuccess()) {
                                emit(it.data to null)
                            } else {
                                emit(null to it.failed)
                            }
                        }
                    } else {
                        emit(null to it.failed)
                    }
                }
            }
        }
    }

    val inviteRecordLiveData = MutableLiveData<InviteRecord>()

    val invitePaging by lazy {
        Pager(config = PagingConfig(10), pagingSourceFactory = {
            InvitePagingSource(inviteRecordLiveData)
        }).flow
    }

    fun getDevices() = apiBase { device("android") }
    fun removeDevices(sessionId: String) = apiBase { removeDevice(sessionId) }
}


