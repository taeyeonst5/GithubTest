package com.allen_chou.githubtest.api

import android.net.UrlQuerySanitizer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object MyRetrofit {

    var lastPage: Int = 1
        private set

    private const val BASE_URL = "https://api.github.com/"
    private const val HEADER_LINK = "link"
    private const val SPLIT_TEXT = ","
    private const val CONTAINS_LAST = "rel=\"last\""
    private const val QUERY_PARAM = "page"

    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(mOkHttpClientBuilder)
            .addConverterFactory(MoshiConverterFactory.create())
    }

    private val mOkHttpClientBuilder: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor {
                val request = it.request().newBuilder().build()
                val response = it.proceed(request)
                response.header(HEADER_LINK)?.let { link ->
                    parsePageCount(link)
                }
                response
            }
            .build()
    }

    private fun parsePageCount(linkHeader: String) {
        val links = linkHeader.split(SPLIT_TEXT)

        //getLastPage
        links.find {
            it.contains(CONTAINS_LAST)
        }?.let {
            lastPage = getPageCount(it)
        }
    }

    private fun getPageCount(it: String): Int {
        //get url
        val url = it.run {
            substring(this.indexOf("<") + 1, lastIndexOf(">"))
        }
        //get queryValue:page
        val sanitizer = UrlQuerySanitizer()
        sanitizer.allowUnregisteredParamaters = true
        sanitizer.parseUrl(url)
        return sanitizer.getValue(QUERY_PARAM).toInt()
    }

    val getApiService: ApiService by lazy {
        retrofitBuilder.build().create(ApiService::class.java)
    }
}