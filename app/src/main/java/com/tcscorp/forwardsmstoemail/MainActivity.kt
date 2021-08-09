package com.tcscorp.forwardsmstoemail

import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.tcscorp.forwardsmstoemail.ui.SMSForwarderApp
import com.tcscorp.forwardsmstoemail.workers.PendingSMSForwardWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Schedule Worker to fetch and forward any pending messages
        // in the local database in background
        scheduleWorker()

        // This method prompts user to select the app
        // as the default messaging app. This is necessary to make
        // the app listen any incoming messages in background even
        // if the app is killed
        showDefaultSmsDialog(this)
        setContent {
            SMSForwarderApp()
        }
    }

    private fun scheduleWorker() {
        val periodicWorkRequest =
            PeriodicWorkRequestBuilder<PendingSMSForwardWorker>(1, TimeUnit.MINUTES).build()
        workManager.enqueue(periodicWorkRequest)
    }

    private fun showDefaultSmsDialog(context: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(RoleManager::class.java) as RoleManager
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
            context.startActivityForResult(intent, 42389)
        } else {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.packageName)
            context.startActivity(intent)
        }
    }

}