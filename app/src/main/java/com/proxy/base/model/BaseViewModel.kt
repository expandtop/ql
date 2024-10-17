package com.proxy.base.model

import com.proxy.base.data.BaseResponse
import com.proxy.base.repo.API
import com.proxy.base.repo.Remote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.flow

abstract class BaseViewModel : CoroutineScope by MainScope() {

    protected fun <T> apiBase(block: suspend API.() -> BaseResponse<T>) = flow {
        val responseData = Remote.callBase(block)
        emit(responseData)
    }

    protected fun <T> api(block: suspend API.() -> T) = flow {
        val responseData = Remote.call(block)
        emit(responseData)
    }

}

