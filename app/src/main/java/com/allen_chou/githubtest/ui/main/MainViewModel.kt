package com.allen_chou.githubtest.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.allen_chou.githubtest.api.User
import com.allen_chou.githubtest.paging.NetWorkState
import com.allen_chou.githubtest.paging.UserRepository

class MainViewModel : ViewModel() {

    var pagedList: LiveData<PagedList<User>> = MutableLiveData()
        private set

    var netWorkLiveData: LiveData<NetWorkState> = MutableLiveData()
        private set

    fun query(query: String) {
        val user = UserRepository().getUsers(viewModelScope, query)
        pagedList = user.pagedList
        netWorkLiveData = user.networkState
    }
}
