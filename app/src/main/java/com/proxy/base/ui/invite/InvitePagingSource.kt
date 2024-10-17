package com.proxy.base.ui.invite

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.proxy.base.data.InviteRecord
import com.proxy.base.data.InviteReward
import com.proxy.base.repo.Remote

/**
 * 2024/9/9
 */
class InvitePagingSource(private val liveData: MutableLiveData<InviteRecord>) :
    PagingSource<Int, InviteReward>() {
    override fun getRefreshKey(state: PagingState<Int, InviteReward>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, InviteReward> {
        val pageNum = params.key ?: 1
        val pageSize = params.loadSize
        val data = Remote.callBase { inviteRecord(pageNum, pageSize) }
        return if (data.isSuccess()) {
            if (pageNum == 1) liveData.postValue(data.data)
            val nextPage = if (data.data.details.size < pageSize) null else pageNum + 1
            LoadResult.Page(data.data.details, null, nextPage)
        } else {
            LoadResult.Error(Throwable(data.failed.msg))
        }
    }
}