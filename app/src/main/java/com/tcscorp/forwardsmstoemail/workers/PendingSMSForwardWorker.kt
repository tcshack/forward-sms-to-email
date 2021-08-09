package com.tcscorp.forwardsmstoemail.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tcscorp.forwardsmstoemail.*
import com.tcscorp.forwardsmstoemail.data.Message
import com.tcscorp.forwardsmstoemail.data.MessageDao
import com.tcscorp.forwardsmstoemail.receiver.SmsReceiver
import com.tcscorp.forwardsmstoemail.util.emailProps
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@HiltWorker
class PendingSMSForwardWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val messageDao: MessageDao,
) : CoroutineWorker(context, params) {
    private lateinit var mSession: Session

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val pendingMessages = messageDao.getAll().filter { !it.forwarded }
        if (pendingMessages.isNotEmpty()) {
            sendEmail(pendingMessages)
        }
        Result.success()
    }

    private suspend fun sendEmail(pendingMessages: List<Message>): Result {
        mSession = Session.getDefaultInstance(emailProps)
        try {
            val mm = MimeMessage(mSession)
            mm.setFrom(InternetAddress(FROM))
            mm.addRecipient(
                javax.mail.Message.RecipientType.TO,
                InternetAddress(RECIPIENT)
            )
            mm.subject = SUBJECT

            // Forward individual messages in the list
            pendingMessages.forEach { message ->
                val updatedMessage = message.copy(forwarded = true)
                mm.setText(message.body)
                val transport = mSession.getTransport(PROTOCOL)
                transport.connect(HOST, USER, PASSWORD)
                transport.sendMessage(mm, mm.allRecipients)
                transport.close()
                messageDao.update(updatedMessage)
            }
            return Result.success()
        } catch (e: Exception) {
            SmsReceiver.isWorkComplete.emit(true)
            return Result.failure()
        }
    }
}