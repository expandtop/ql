package com.proxy.base.data

import com.proxy.base.BuildConfig
import com.proxy.base.util.secondExpire
import ex.ss.lib.base.adapter.data.BaseItem
import ex.ss.lib.base.adapter.loadmore.data.LoadMoreItem
import ex.ss.lib.tools.extension.ByteUnit
import ex.ss.lib.tools.extension.formatByte
import ex.ss.lib.tools.extension.formatByteUnit
import ex.ss.lib.tools.extension.formatCent
import kotlin.math.max

data class LoginData(
    val token: String,
    val auth_data: String,
    val session_id: String,
)

data class UserInfo(
    val avatar_url: String,
    val invite_user_id: String?,
    val id: Int,
    val balance: Int,
    val banned: Int,
    val commission_balance: Int,
    val created_at: Int,
    val email: String,
    val expired_at: Int?,
    val last_login_at: Int,
    val plan_id: Int,
    val remind_expire: Int,
    val remind_traffic: Int,
    val transfer_enable: Long,
    val uuid: String,
) {

    fun isExpire(): Boolean {
        if (expired_at == null) return true
        return expired_at.secondExpire()
    }

    fun isGuest(): Boolean {
        return email.startsWith("游客")
    }
}

data class Notice(
    val content: String,
    val created_at: Int,
    val id: Int,
    val show: Int,
    val title: String,
    val updated_at: Int,
) : BaseItem

data class NavCate(val id: Int, val name: String) : BaseItem

data class NavIndex(val id: Int, val name: String, val cover: String, val link: String) : BaseItem

data class SubscribeInfo(
    val d: Long,
    val email: String,
    val expired_at: Int?,
    val plan: Plan,
    val plan_id: Int,
    val reset_day: Int,
    val subscribe_url: String,
    val subscribe_route: String,
    val token: String,
    val transfer_enable: Long,
    val u: Long,
    val uuid: String,
) {

    fun subExpireTime() = expired_at ?: 0

    fun isExpire(): Boolean {
        if (expired_at != null) {
            return expired_at.secondExpire()
        }
        return false
    }

    fun usedTransfer(): Pair<String, ByteUnit> {
        return (u + d).formatByte(2)
    }

    fun enableTransfer(): String {
        return transfer_enable.formatByteUnit(ByteUnit.GB)
    }

    fun isOverTransfer(): Boolean {
        return transfer_enable <= (u + d)
    }
}

data class Plan(
    val content: String,
    val created_at: Int,
    val group_id: Int,
    val half_year_price: Int?,
    val id: Int,
    val month_price: Int?,
    val name: String,
    val onetime_price: Int?,
    val quarter_price: Int?,
    val renew: Int,
    val reset_price: Int?,
    val show: Int,
    val sort: Int,
    val speed_limit: Int,
    val three_year_price: Int?,
    val transfer_enable: Int,
    val two_year_price: Int?,
    val updated_at: Int,
    val year_price: Int?,
) : BaseItem {
    fun getPrice(period: String): Int {
        return when (period) {
            "month_price" -> month_price ?: 0
            "quarter_price" -> quarter_price ?: 0
            "half_year_price" -> half_year_price ?: 0
            "year_price" -> year_price ?: 0
            "two_year_price" -> two_year_price ?: 0
            "three_year_price" -> three_year_price ?: 0
            else -> 0
        }
    }
}

data class PlanStartPayItem(
    val name: String,
    val price: Int,
    val period: String,
    val planId: Int,
    val isSelect: Boolean,
) : BaseItem

data class PlanPayItem(
    val plan: Plan,
    val period_name: String,
    val name: String,
    val price: Int,
) : BaseItem {
    var isSelect: Boolean = false
    override fun <T : BaseItem> areContentsTheSame(other: T): Boolean {
        if (other !is PlanPayItem) return false
        if (other.period_name != period_name) return false
        if (other.name != name) return false
        if (other.price != price) return false
        if (other.isSelect != isSelect) return false
        return true
    }
}

data class CommConfig(
    val customer_service_address: String,
    val adimageurl: String,
    val official_website_address: String,
    val Invitation_activity_copywriting: String,
    val binding_instructions: String,
)

data class CommChannelConfig(
    val customer_service_address: String,
    val telegram_discuss_link: String,
    val official_website_address: String,
    val Anti_fraud_Tips: String,
    val payment_announcement: String,
)

data class VersionInfo(
    val appname: String,
    val arch: Any,
    val browser_download: Int,
    val channel: String,
    val created_at: String,
    val explain: String,
    val force: Int, //force 1 强制更新
    val id: Int,
    val link: String,
    val updated_at: String,
    val updatemethod: String,
    val version: String,
) {

    fun hasNewVersion(): Boolean { //1.1.9 - 1.2.0
        //10109 - 10200
        return runCatching {
            convertVersion(version) > convertVersion(BuildConfig.VERSION_NAME)
        }.getOrElse {
            version != BuildConfig.VERSION_NAME
        }
    }

    //1.0 => 10000
    //1.0.1 => 10001
    //1.0.0.1 => 10000
    private fun convertVersion(ver: String): Int {
        return when {
            ver.length == 5 -> ver
            ver.length < 5 -> ver.padEnd(5, '.')
            ver.length > 5 -> ver.substring(0, 5)
            else -> "....."
        }.let { it.replace(".", "0").toInt() }
    }
}


/**
 * "stat": [
 * 			0, // 已注册用户数
 * 			0, // 有效佣金
 * 			0,// 确认中的佣金
 * 			10, // 佣金比例
 * 			0, // 可用佣金
 * 			0 // 有效邀请人数
 * 		]
 */
data class InviteInfo(
    val codes: List<InviteCodes>, val stat: List<Int>, val invited_link: String,
) {
    fun getInviteCode(): String {
        return codes.getOrNull(0)?.code ?: ""
    }
}

data class InviteCodes(
    val code: String,
    val created_at: Int,
    val id: Int,
    val pv: Int,
    val status: Int,
    val updated_at: Int,
    val user_id: Int,
)

data class InviteRecord(
    val total: Int,
    val details: List<InviteReward>,
)

data class InviteReward(
    val is_effective: Boolean,
    val created_at: String,
    val email: String,
) : LoadMoreItem

data class PaymentMethod(
    val id: Int,
    val name: String,
    val payment: String,
    val handling_fee_fixed: Int?,
    val handling_fee_percent: String?,
    val isSelect: Boolean = false,
) : BaseItem {
    //手续费=  order.total_amount*payment.handling_fee_percent + payment.handling_fee_fixed
    fun fee(price: Int): Int {
        return (price * (handling_fee_percent?.toFloat()
            ?: 0F) / 100F).toInt() + (handling_fee_fixed ?: 0)
    }

    override fun <T : BaseItem> areContentsTheSame(other: T): Boolean {
        if (other !is PaymentMethod) return false
        if (other.id != id) return false
        if (other.name != name) return false
        if (other.isSelect != isSelect) return false
        return true
    }
}

data class CouponData(
    val code: String,
    val created_at: Int,
    val ended_at: Int,
    val id: Int,
    val name: String,
    val show: Int,
    val started_at: Int,
    val type: Int, //1 按金额  2 按比例优化
    val updated_at: Int,
    val value: Float, //优惠金额（或%比例）
    val limit_plan_ids: List<Int>?, //显示Plan Id
    val limit_period: List<String>?, //显示Plan period
) {
    fun discount(originPrice: Int): Int {
        return when (type) {
            TYPE_AMOUNT -> {
                max(0, (originPrice - value).toInt())
            }

            TYPE_SCALE -> {
                max(0, (originPrice * (1 - value * 0.01)).toInt())
            }

            else -> originPrice
        }
    }

    fun discountPrice(originPrice: Int): Int {
        return when (type) {
            TYPE_AMOUNT -> {
                maxOf(0, value.toInt())
            }

            TYPE_SCALE -> {
                max(0, (originPrice * (value * 0.01)).toInt())
            }

            else -> 0
        }
    }

    fun show(): String {
        return when (type) {
            TYPE_AMOUNT -> "${value.toInt().formatCent()}¥"
            TYPE_SCALE -> "$value%"
            else -> "0"
        }
    }

    companion object {
        const val TYPE_AMOUNT = 1
        const val TYPE_SCALE = 2
    }
}

data class PayOrder(
    val created_at: Int,
    val payment_id: Int?,
    val coupon_id: Int?,
    val period: String,
    val plan_id: Int,
    val status: Int,  //订单状态 0待支付1开通中2已取消3已完成4已折抵
    val total_amount: Int,
    val discount_amount: Int?,
    val trade_no: String,
    val updated_at: Int,
    val plan: Plan,
) : BaseItem {

    override fun <T : BaseItem> areContentsTheSame(other: T): Boolean {
        if (other !is PayOrder) return false
        if (other.trade_no != trade_no) return false
        if (other.plan.name != plan.name) return false
        if (other.total_amount != total_amount) return false
        if (other.created_at != created_at) return false
        if (other.status != status) return false
        return true
    }
}

data class OrderCheckout(val type: Int, val data: String)

data class AboutTos(
    val content: String,
    val created_at: String,
    val id: Int,
    val identifier: String,
    val status: Int,
    val title: String,
    val updated_at: String,
)

data class HelpDetail(
    val content: String,
    val created_at: String,
    val id: Int,
    val clent_type: Int,
    val status: Int,
    val title: String,
    val updated_at: String,
) : BaseItem


data class Rebates(
    val commission: List<Commission>,
)

//"id": 1,
//"title": "第一梯队",
//"min": 0,
//"max": 10,
//"proportion": 10,
//"is_here": 1
data class Commission(
    val id: Int,
    val title: String,
    val min: Int,
    val max: Int,
    val proportion: Int,
    val is_here: Int,
) : BaseItem

data class DeviceItem(
    val ip: String,
    val login_at: Int,
    val ua: String,
    val device_name: String,
    val session_id: String = "",
    val isCurrent: Boolean = false,
) : BaseItem


data class UnitAd(
    val channel: String?,
    val cover: String?,
    val cover_type: Int,
    val download: Int,
    val link: String?,
    val tagline: String?,
) {
    fun isImageAd() = !cover.isNullOrEmpty()
    fun isTextAd() = cover.isNullOrEmpty() && !tagline.isNullOrEmpty()
}

data class TicketItem(
    val created_at: Int,
    val id: Int,
    val level: Int,
    val message: List<TicketItemMessage>?,
    val reply_status: Int,
    val status: Int,
    val subject: String,
    val updated_at: Int,
    val user_id: Int,
) : BaseItem

data class TicketItemMessage(
    val created_at: Int,
    val id: Int,
    val is_me: Boolean,
    val message: String,
    val ticket_id: Int,
    val updated_at: Int,
) : BaseItem

data class WithDrawConfig(val withdraw_methods: List<String>)