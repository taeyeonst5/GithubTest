package com.allen_chou.githubtest.extensions

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.allen_chou.githubtest.BuildConfig

private const val ALLEN_LOG_TAG = "beta"

fun logd(message: String) {
    if (BuildConfig.DEBUG) {
        Log.d(ALLEN_LOG_TAG, message)
    }
}

fun loge(message: String) {
    if (BuildConfig.DEBUG) {
        Log.e(ALLEN_LOG_TAG, message)
    }
}

fun Activity.hideSoftKeyboard(im: InputMethodManager) {
    currentFocus?.let {
        im.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun Activity.checkNetworkIsConnect(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    } else {
        logd("checkInternet api23 below")
        val info = manager.activeNetworkInfo
        return (info != null && info.isConnectedOrConnecting)
    }
}