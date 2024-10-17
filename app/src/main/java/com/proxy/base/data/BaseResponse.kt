package com.proxy.base.data

data class BaseResponse<T>(
    val status: String, //success 成功
    val message: String,
    val data: T,
) {
    companion object {
        private const val SUCCESS = "success"
    }

    fun isSuccess(): Boolean = status == SUCCESS
}

data class BaseDataResponse<T>(
    val data: T,
)