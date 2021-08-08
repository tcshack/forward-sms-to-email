package com.tcscorp.forwardsmstoemail.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tcscorp.forwardsmstoemail.util.isOnline

class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            if (isOnline(context)) {
                // Connected to an active network.
            } else {
                // Not connected to an active network. Now, enable Wi-Fi
            }
        } catch (ignore: Exception) {

        }
    }
}