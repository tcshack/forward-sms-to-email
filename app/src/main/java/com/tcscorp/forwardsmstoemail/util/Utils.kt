package com.tcscorp.forwardsmstoemail.util

import android.content.Context
import android.net.ConnectivityManager


fun isOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = cm.activeNetworkInfo
    //should check null because in airplane mode it will be null
    return netInfo != null && netInfo.isConnected
}