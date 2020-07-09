package com.allen_chou.githubtest.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    //    ex:domain/search/users?q=to in:login type:user&page=34&per_page=30
    @GET("/search/users") //in login: search username
    suspend fun getUsers(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") limit: Int
    ): UserResponse
}