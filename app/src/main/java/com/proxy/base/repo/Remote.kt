package com.proxy.base.repo

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.proxy.base.config.UserConfig
import com.proxy.base.data.BaseResponse
import com.proxy.base.util.AppToast
import ex.ss.lib.components.log.SSLog
import ex.ss.lib.net.BaseRemoteRepository
import ex.ss.lib.net.bean.ResponseData
import ex.ss.lib.net.bean.ResponseFailed
import kotlinx.coroutines.delay
import org.json.JSONObject
import retrofit2.HttpException
import java.net.UnknownHostException

object Remote : BaseRemoteRepository<API>() {

    private val log by lazy { SSLog.create("Remote") }

    val loginExpireLiveData = MutableLiveData<Boolean>()

    override val apiClass: Class<API> = API::class.java

    suspend fun <T> call(block: suspend API.() -> T): ResponseData<T> {
        val response = retryCall(0) { block.invoke(api) }
        return if (response.isSuccess()) {
            ResponseData.success(response.data)
        } else {
            val failed = failedWrapper(response.failed)
            log.d("${failed.code} ${failed.msg} ${failed.e}")
            if (failed.code == 403) {
                postLoginExpire()
            }
            showFailedToast(failed.msg ?: failed.e?.message ?: "")
            ResponseData.failed(failed)
        }
    }

    suspend fun <T> callBase(block: suspend API.() -> BaseResponse<T>): ResponseData<T> {
        val response = retryCall(0) { block.invoke(api) }
        return if (response.isSuccess()) {
            val baseResponse = response.data
            if (baseResponse.isSuccess()) {
                ResponseData.success(200, baseResponse.message, baseResponse.data)
            } else {
                ResponseData.failed(ResponseFailed(-1, baseResponse.message))
            }
        } else {
            val failed = failedWrapper(response.failed)
            log.d("${failed.code} ${failed.msg} ${failed.e}")
            if (failed.code == 403) {
                postLoginExpire()
            }
            showFailedToast(failed.msg ?: failed.e?.message ?: "")
            ResponseData.failed(failed)
        }
    }

    private suspend fun <T> retryCall(retryCount: Int = 3, call: suspend () -> T): ResponseData<T> {
        val callResponse = call(call)
        return if (!callResponse.isSuccess() && retryCount > 0) { //网络请求失败
            delay(100)
            retryCall(retryCount - 1, call)
        } else {
            callResponse
        }
    }

    private fun showFailedToast(message: String) {
        if (message.isEmpty()) return
        if (message.equals("timeout", true)) return
        AppToast.show(message)
    }

    private fun postLoginExpire() = synchronized(loginExpireLiveData) {
        if (UserConfig.isLogin()) {
            UserConfig.logout()
            loginExpireLiveData.postValue(true)
        }
    }

    fun resetLoginExpire() {
        loginExpireLiveData.postValue(false)
    }

    private fun failedWrapper(failed: ResponseFailed): ResponseFailed {
        return when (val error = failed.e) {
            is UnknownHostException -> {
                val errorMessage = errorMessage[UnknownHostException::class.java]
                ResponseFailed(failed.code, errorMessage, error)
            }

            is HttpException -> {
                val code = error.code()
                val errorMessage = error.response()?.errorBody()?.string() ?: error.message()
                val message = parseErrorMessage(errorMessage)
                ResponseFailed(code, message, error)
            }

            else -> failed
        }
    }

    private val errorMessage = mutableMapOf<Class<out Exception>, String>()

    fun initErrorMessage(context: Context) {
//        errorMessage[UnknownHostException::class.java] = context.getString(R.string.unknown_host)
    }

    private fun parseErrorMessage(errorMessage: String): String {
        return runCatching {
            JSONObject(errorMessage).optString("message") ?: errorMessage
        }.getOrElse {
            errorMessage
        }
    }

}