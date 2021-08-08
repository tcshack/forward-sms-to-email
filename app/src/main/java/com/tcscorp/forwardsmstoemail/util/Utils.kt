package com.tcscorp.forwardsmstoemail.util

import android.content.Context
import android.net.ConnectivityManager


fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return try {
        val netInfo = connectivityManager.activeNetworkInfo
        netInfo != null && netInfo.isConnected
    } catch (e: NullPointerException) {
        false
    }
}