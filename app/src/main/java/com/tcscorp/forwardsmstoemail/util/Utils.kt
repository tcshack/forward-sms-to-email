package com.tcscorp.forwardsmstoemail.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.tcscorp.forwardsmstoemail.HOST
import com.tcscorp.forwardsmstoemail.PASSWORD
import com.tcscorp.forwardsmstoemail.PORT
import com.tcscorp.forwardsmstoemail.USER
import java.util.*

val emailProps = Properties().apply {
    setProperty("mail.smtp.host", HOST)
    setProperty("mail.smtp.port", PORT)
    setProperty("mail.smtp.user", USER)
    setProperty("mail.smtp.password", PASSWORD)
    setProperty("mail.smtp.starttls.enable", "true")
    setProperty("mail.smtp.auth", "true")
}

fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return try {
        val netInfo = connectivityManager.activeNetworkInfo
        netInfo != null && netInfo.isConnected
    } catch (e: NullPointerException) {
        false
    }
}

fun enableWifi(context: Context) {
    val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
    if (wifiManager != null) {
        wifiManager.isWifiEnabled = true
    }
}