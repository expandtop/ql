package com.proxy.base.ui.pay

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.proxy.base.data.PayOrder
import com.proxy.base.repo.Remote

/**
 * 2024/9/9
 */
class OrderPagingSource : PagingSource<Int, PayOrder>() {
    override fun getRefreshKey(state: PagingState<Int, PayOrder>): Int? {
        return null
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PayOrder> {
        val pageNum = params.key ?: 1
        val pageSize = params.loadSize
        val data = Remote.callBase { orderList(pageNum, pageSize) }
        return if (data.isSuccess()) {
            val nextPage = if (data.data.size < pageSize) null else pageNum + 1
            LoadResult.Page(data.data, null, nextPage)
        } else {
            LoadResult.Error(Throwable(data.failed.msg))
        }
    }
}