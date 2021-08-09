package com.tcscorp.forwardsmstoemail.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.work.*
import com.tcscorp.forwardsmstoemail.*
import com.tcscorp.forwardsmstoemail.data.PreferenceManager
import com.tcscorp.forwardsmstoemail.data.Result
import com.tcscorp.forwardsmstoemail.util.enableWifi
import com.tcscorp.forwardsmstoemail.workers.SendEmailWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {

    companion object {
        // Object used to fire when work is complete (Successful or Failed)
        // When the emitted value is set to true, we release our pending
        // intent to process other incoming messages.
        val isWorkComplete = MutableStateFlow(false)
    }

    @Inject
    lateinit var preferencesManager: PreferenceManager

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var scope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        Telephony.Sms.Intents.getMessagesFromIntent(intent)?.let { messages ->
            val pendingResult = goAsync()
            val address = messages[0].displayOriginatingAddress

            scope.launch {
                isWorkComplete.collect { isComplete ->
                    if (isComplete) {
                        // Release the pending intent so, to process other incoming messages.
                        pendingResult.finish()
                    }
                }
            }

            scope.launch {
                preferencesManager.preferencesFlow.collect {
                    if (it is Result.Success) {
                        val settings = it.data
                        if (settings.phoneNumber == address) {
                            val dateSent = messages[0].timestampMillis * 1000L
                            val body: String = messages
                                .mapNotNull { message -> message.displayMessageBody }
                                .reduce { body, new -> body + new }
                            if (body == settings.secretText) {
                                // Enable Wi-Fi
                                enableWifi(context)
                            } else {
                                // Forward SMS
                                sendEmail(address, dateSent, body).enqueue()
                            }
                        } else {
                            pendingResult.finish()
                        }
                    }
                }
            }
        }
    }

    private fun sendEmail(
        address: String,
        dateSent: Long,
        body: String
    ): WorkContinuation {
        val builder = Data.Builder()
        builder.putString(KEY_MESSAGE_BODY, body)
        builder.putString(KEY_SENDER_ADDRESS, address)
        builder.putLong(KEY_DATE_SENT, dateSent)
        return workManager
            .beginUniqueWork(
                SEND_EMAIL_WORK,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                OneTimeWorkRequestBuilder<SendEmailWorker>()
                    .addTag(FORWARD_SMS)
                    .setInputData(builder.build())
                    .build()
            )
    }
}