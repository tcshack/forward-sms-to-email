package com.tcscorp.forwardsmstoemail.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony.Sms
import android.telephony.SmsMessage
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.tcscorp.forwardsmstoemail.workers.SendEmailWorker
import javax.inject.Inject

const val SEND_EMAIL_WORK = "send_email"

class SmsReceiver @Inject constructor(
    private val workManager: WorkManager
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Sms.Intents.getMessagesFromIntent(intent)?.let { messages ->

            val pendingResult = goAsync()
            val data = workManager.getWorkInfosByTag("oneTimeWorkRequest.id").get()[0]
            sendEmail(messages[0])
            if (data.state.isFinished) {
                pendingResult.finish()
            }
        }
    }

    private fun sendEmail(smsMessage: SmsMessage) {
        var continuation = workManager
            .beginUniqueWork(
                SEND_EMAIL_WORK,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                OneTimeWorkRequest.from(SendEmailWorker::class.java)
            )
            .enqueue()
    }

}