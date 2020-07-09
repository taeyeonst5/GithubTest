package com.allen_chou.githubtest.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.allen_chou.githubtest.api.ApiService
import com.allen_chou.githubtest.api.MyRetrofit
import com.allen_chou.githubtest.api.User
import com.allen_chou.githubtest.extensions.logd
import com.allen_chou.githubtest.extensions.loge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class UserDataSource(private val scope: CoroutineScope, private val query: String) :
    PageKeyedDataSource<Int, User>() {

    var netWorkState: MutableLiveData<NetWorkState> = MutableLiveData()
    var retry: (() -> Any)? = null

    private var apiService: ApiService = MyRetrofit.getApiService


    companion object {
        private const val QUERY_QUALIFIERS = "in:login type:user"
        private var INIT_PAGE = 1
        const val TEXT_UNKNOWN = "Unknown"
        const val TEXT_IOE = "IOE"
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, User>
    ) {
        logd("loadInitial")
        var list: List<User>? = null
        netWorkState.postValue(NetWorkState.LOADING)

        scope.launch {
            val apiJob = scope.async(Dispatchers.IO) {
                try {
                    list = callApi(INIT_PAGE, params.requestedLoadSize)
                    logd("loadInitial size ${list?.size}")
                    retry = null
                } catch (t: Throwable) {
                    retry = {
                        loadInitial(params, callback)
                    }

                    loge("call api fail ${t.message}")
                    handleError(t)
                    return@async
                }
            }
            apiJob.await()
            list?.let {
                callback.onResult(it, null, INIT_PAGE)
                netWorkState.postValue(NetWorkState.FINISH(it.size, true))
            }
        }
    }

    private suspend fun callApi(page: Int, perPage: Int): List<User> {
        return apiService.getUsers(
            createQueryParam(),
            page,
            perPage
        ).items
    }

    private fun handleError(t: Throwable) {
        val errorMsg = when (t) {
            is IOException -> TEXT_IOE
            is HttpException -> t.message ?: TEXT_UNKNOWN
            else -> TEXT_UNKNOWN
        }
        netWorkState.postValue(NetWorkState.FAILED(retry, errorMsg))
    }

    private fun createQueryParam() = "$query $QUERY_QUALIFIERS"

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
        val nextPage = params.key + 1
        val lastPage = MyRetrofit.lastPage //get lastPageCount from header link
        logd("loadAfter key:${params.key},nextPage:$nextPage,lastPage:$lastPage")
        if (nextPage > lastPage) return

        var list: List<User>? = listOf()
        netWorkState.postValue(NetWorkState.LOADING)
        scope.launch {
            val loadAfterJob = scope.async(Dispatchers.IO) {
                try {
                    list = callApi(nextPage, params.requestedLoadSize)
                    logd("loadAfter size ${list?.size},page:$nextPage")
                    retry = null
                } catch (t: Throwable) {
                    retry = {
                        loadAfter(params, callback)
                    }
                    loge("loadAfter call api fail ${t.message}")
                    handleError(t)
                    list = null
                    return@async
                }

            }
            loadAfterJob.await()
            list?.let {
                callback.onResult(it, nextPage)
                netWorkState.postValue(NetWorkState.FINISH(it.size, false))
            }
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, User>
    ) {
        //we don't use.
    }
}

sealed class NetWorkState {
    object LOADING : NetWorkState()
    data class FINISH(val dataSize: Int, val isInitLoad: Boolean) : NetWorkState()
    data class FAILED(val retry: (() -> Any)?, val errorMsg: String) : NetWorkState()
}