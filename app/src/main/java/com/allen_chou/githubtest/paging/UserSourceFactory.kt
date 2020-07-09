package com.allen_chou.githubtest.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.allen_chou.githubtest.api.User
import com.allen_chou.githubtest.extensions.logd
import kotlinx.coroutines.CoroutineScope

class UserSourceFactory(private val scope: CoroutineScope, private val query: String) :
    DataSource.Factory<Int, User>() {
    val sourceLiveData = MutableLiveData<UserDataSource>()
    override fun create(): DataSource<Int, User> {
        val source = UserDataSource(scope, query)
        logd("create UserDataSource")
        sourceLiveData.postValue(source)
        return source
    }

}