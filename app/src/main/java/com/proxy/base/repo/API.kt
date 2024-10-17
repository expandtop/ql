package com.proxy.base.repo

import com.proxy.base.data.AboutTos
import com.proxy.base.data.BaseDataResponse
import com.proxy.base.data.BaseResponse
import com.proxy.base.data.BindEmail
import com.proxy.base.data.BindInviteCodeBody
import com.proxy.base.data.CommChannelConfig
import com.proxy.base.data.CommConfig
import com.proxy.base.data.CouponCheckBody
import com.proxy.base.data.CouponData
import com.proxy.base.data.DeviceItem
import com.proxy.base.data.DeviceLoginBody
import com.proxy.base.data.ForgetPwdBody
import com.proxy.base.data.HelpDetail
import com.proxy.base.data.InviteInfo
import com.proxy.base.data.InviteRecord
import com.proxy.base.data.LoginData
import com.proxy.base.data.LoginPwdBody
import com.proxy.base.data.NavCate
import com.proxy.base.data.NavIndex
import com.proxy.base.data.Notice
import com.proxy.base.data.OrderCancelBody
import com.proxy.base.data.OrderCheckout
import com.proxy.base.data.OrderCheckoutBody
import com.proxy.base.data.OrderSaveBody
import com.proxy.base.data.PayOrder
import com.proxy.base.data.PaymentMethod
import com.proxy.base.data.Plan
import com.proxy.base.data.Rebates
import com.proxy.base.data.RegisterBody
import com.proxy.base.data.ResetPwdBody
import com.proxy.base.data.SmsCodeBody
import com.proxy.base.data.SubscribeInfo
import com.proxy.base.data.TicketClose
import com.proxy.base.data.TicketItem
import com.proxy.base.data.TicketReply
import com.proxy.base.data.TicketSave
import com.proxy.base.data.TransferBody
import com.proxy.base.data.UserCloseBody
import com.proxy.base.data.UnitAd
import com.proxy.base.data.UserInfo
import com.proxy.base.data.VersionInfo
import com.proxy.base.data.WithDrawBody
import com.proxy.base.data.WithDrawConfig
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface API {

    @POST("/passport/auth/registerByMac")
    suspend fun deviceLogin(@Body body: DeviceLoginBody): BaseResponse<LoginData>

    @POST("/user/sign")
    suspend fun sign(): BaseResponse<String>

    @POST("/user/checkSign")
    suspend fun checkSign(): BaseResponse<Boolean>

    @GET("/user/nav/cate")
    suspend fun navCate(): BaseResponse<List<NavCate>>

    @GET("/user/nav/index")
    suspend fun navIndex(@Query("cate_id") id: Int): BaseResponse<List<NavIndex>>

    @GET("/passport/getserversettings")
    suspend fun commConfig(): BaseResponse<CommConfig>

    @GET("/passport/comm/configchannel")
    suspend fun commChannelConfig(): BaseResponse<CommChannelConfig>

    @POST("/passport/auth/register")
    suspend fun register(@Body body: RegisterBody): BaseResponse<LoginData>

    @POST("/passport/comm/sendEmailVerify")
    suspend fun getSmsCode(@Body body: SmsCodeBody): BaseResponse<Boolean>

    @POST("/passport/auth/login")
    suspend fun loginPwd(@Body body: LoginPwdBody): BaseResponse<LoginData>

    @POST("/user/CloseAccount")
    suspend fun loginClose(@Body body: UserCloseBody): BaseResponse<String>

    @POST("/passport/auth/forget")
    suspend fun forgetPwd(@Body body: ForgetPwdBody): BaseResponse<Any>

    @POST("/user/changePassword")
    suspend fun resetPwd(@Body body: ResetPwdBody): BaseResponse<Boolean?>

    @GET("/user/info")
    suspend fun userInfo(): BaseResponse<UserInfo>

    @GET("/user/notice/fetch")
    suspend fun notice(): BaseDataResponse<List<Notice>>

    @GET("/user/getSubscribe")
    suspend fun getSubscribe(): BaseResponse<SubscribeInfo>

    @GET("/user/resetSecurity")
    suspend fun resetSubscribe(): BaseResponse<String>

    @GET("/user/plan/fetch")
    suspend fun planFetch(): BaseResponse<List<Plan>>

    @GET("/user/order/getPaymentMethod")
    suspend fun getPaymentMethod(): BaseResponse<List<PaymentMethod>>

    @POST("/user/coupon/check")
    suspend fun couponCheck(@Body body: CouponCheckBody): BaseResponse<CouponData>

    @POST("/user/order/save")
    suspend fun orderSave(@Body body: OrderSaveBody): BaseResponse<String>

    @POST("/user/order/checkout")
    suspend fun orderCheckout(@Body body: OrderCheckoutBody): OrderCheckout

    @GET("/user/order/fetch")
    suspend fun orderList(
        @Query("page") current: Int, @Query("pageSize") pageSize: Int,
    ): BaseResponse<List<PayOrder>>

    @POST("/user/order/cancel")
    suspend fun orderCancel(@Body body: OrderCancelBody): BaseResponse<Boolean>

    @GET("/user/invite/save")
    suspend fun inviteSave(): BaseResponse<Boolean>

    @GET("/user/invite/fetch")
    suspend fun inviteFetch(): BaseResponse<InviteInfo>

    @POST("/user/invite/bind")
    suspend fun inviteBind(@Body body: BindInviteCodeBody): BaseResponse<String>

    @GET("/user/invite/record")
    suspend fun inviteRecord(
        @Query("current") current: Int, @Query("page_size") pageSize: Int,
    ): BaseResponse<InviteRecord>

    @GET("/passport/app/check")
    suspend fun version(@Query("type") type: String,@Query("channel") channel: String): BaseResponse<VersionInfo>

    @GET("/user/aboutus")
    suspend fun aboutusTos(@Query("type") type: String): BaseResponse<List<AboutTos>>

    @POST("/user/bind_email")
    suspend fun bindEmail(@Body body: BindEmail): BaseResponse<String>

    @GET("/user/common")
    suspend fun help(@Query("type") type: String): BaseResponse<List<HelpDetail>>

    @GET("/user/getActiveSession")
    suspend fun device(@Query("type") type: String): BaseResponse<Map<String, DeviceItem>>

    @FormUrlEncoded
    @POST("/user/removeActiveSession")
    suspend fun removeDevice(@Field("session_id") session_id: String): BaseResponse<Any>

    @POST("/user/ticket/save")
    suspend fun ticketSave(@Body body: TicketSave): BaseResponse<String?>

    @POST("/user/ticket/close")
    suspend fun ticketClose(@Body body: TicketClose): BaseResponse<String?>

    @GET("/user/ticket/fetch")
    suspend fun ticketFetch(): BaseResponse<List<TicketItem>>

    @GET("/user/ticket/fetch")
    suspend fun ticketDetail(@Query("id") id: String): BaseResponse<TicketItem>

    @POST("/user/ticket/reply")
    suspend fun ticketReply(@Body body: TicketReply): BaseResponse<String?>

    @GET("/user/rebates")
    suspend fun rebates(): BaseResponse<Rebates>

    @GET("/user/comm/config")
    suspend fun withdrawConfig(): BaseResponse<WithDrawConfig>

    @POST("/user/ticket/withdraw")
    suspend fun withdraw(@Body body: WithDrawBody): BaseResponse<Any>

    @POST("/user/transfer")
    suspend fun transfer(@Body body: TransferBody): BaseResponse<Any>

    @GET("/passport/ad")
    suspend fun splashAd(
        @Query("position") position: String,
        @Query("type") type: String,
        @Query("channel") channel: String
    ): BaseResponse<List<UnitAd>?>

    @GET("/user/ad")
    suspend fun mainAd(
        @Query("position") position: String,
        @Query("type") type: String,
        @Query("channel") channel: String
    ): BaseResponse<List<UnitAd>?>

}