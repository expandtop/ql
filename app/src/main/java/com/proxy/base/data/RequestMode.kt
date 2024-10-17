package com.proxy.base.data

data class DeviceLoginBody(
    val mac: String,
)

data class RegisterBody(
    val email: String,
    val password: String,
    val email_code: String,
    val invite_code: String,
)

data class SmsCodeBody(
    val email: String,
)

data class ForgetPwdBody(
    val email: String,
    val password: String,
    val email_code: String?,
)

data class ResetPwdBody(
    val old_password: String,
    val new_password: String,
)

data class LoginPwdBody(
    val email: String,
    val password: String,
)


data class UserCloseBody(
    val email: String,
    val email_code: String,
)

data class LoginCodeBody(
    val tel: String,
    val code: String,
)

data class BandInviteCodeBody(
    val invite_code: String,
)

data class PayBody(
    val period: String,
    val plan_id: Int,
    val payment_id: Int,
)

data class CouponCheckBody(
    val plan_id: Int,
    val code: String,
)

data class OrderSaveBody(
    val plan_id: Int,
    val coupon_code: String?,
    val period: String,
)

data class OrderCheckoutBody(
    val trade_no: String,
    val method: Int,
)

data class OrderCancelBody(
    val trade_no: String,
)

data class BindInviteCodeBody(
    val invite_code: String,
)

data class BindEmail(
    val email: String,
    val email_code: String,
    val password: String,
)

data class TicketSave(
    val subject: String,
    val level: Int = 2, // //等级  0低 1中 2高
    val message: String,
)

data class TicketClose(
    val id: String,
)

data class TicketReply(
    val id: String,
    val message: String,
)

data class WithDrawBody(
    val withdraw_method: String,
    val withdraw_account: String,
)
data class TransferBody(
    val transfer_amount: Int,
)