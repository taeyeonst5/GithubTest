package com.allen_chou.githubtest.paging

import androidx.lifecycle.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.allen_chou.githubtest.api.User
import com.allen_chou.githubtest.extensions.logd
import kotlinx.coroutines.CoroutineScope

class UserRepository {

    companion object {
        const val PAGED_PAGE_SIZE = 30
    }

    fun getUsers(scope: CoroutineScope, query: String): Listing<User> {
        logd("call UserRepository")
        val userSourceFactory = UserSourceFactory(scope, query)
        val config =
            PagedList.Config.Builder().setInitialLoadSizeHint(PAGED_PAGE_SIZE)
                .setPageSize(PAGED_PAGE_SIZE).setEnablePlaceholders(false)
                .build()
        val livePagedListBuilder = LivePagedListBuilder(userSourceFactory, config).build()
        val netWorkStateLiveData = userSourceFactory.sourceLiveData.switchMap {
            it.netWorkState
        }

        return Listing(
            pagedList = livePagedListBuilder,
            networkState = netWorkStateLiveData
        )
    }
}