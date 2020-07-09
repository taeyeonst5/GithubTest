package com.allen_chou.githubtest.api

data class UserResponse(
    val incomplete_results: Boolean,
    val items: List<User>,
    val total_count: Int
)

data class User(
    val avatar_url: String,
    val id: Int,
    val login: String,
    val node_id: String,
    val type: String,
    val url: String
)