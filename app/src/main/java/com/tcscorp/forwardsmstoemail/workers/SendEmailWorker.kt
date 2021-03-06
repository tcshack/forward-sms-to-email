package com.tcscorp.forwardsmstoemail.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tcscorp.forwardsmstoemail.*
import com.tcscorp.forwardsmstoemail.data.Message
import com.tcscorp.forwardsmstoemail.data.MessageDao
import com.tcscorp.forwardsmstoemail.receiver.SmsReceiver.Companion.isWorkComplete
import com.tcscorp.forwardsmstoemail.util.emailProps
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@HiltWorker
class SendEmailWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val messageDao: MessageDao,
) : CoroutineWorker(context, params) {
    private lateinit var mSession: Session

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        // Receive input data sent from the SmsReceiver broadcast receiver
        val address = inputData.getString(KEY_SENDER_ADDRESS)
        val body = inputData.getString(KEY_MESSAGE_BODY)
        val dateSent = inputData.getLong(KEY_DATE_SENT, 0)
        val message = Message(address!!, body!!, dateSent)
        messageDao.insert(message)
        sendEmail(message)
    }

    private suspend fun sendEmail(message: Message): Result {
        mSession = Session.getDefaultInstance(emailProps)
        try {
            val mm = MimeMessage(mSession)
            mm.setFrom(InternetAddress(FROM))
            mm.addRecipient(
                javax.mail.Message.RecipientType.TO,
                InternetAddress(RECIPIENT)
            )
            mm.subject = SUBJECT
            mm.setText(message.body)
            val transport = mSession.getTransport(PROTOCOL)
            transport.connect(HOST, USER, PASSWORD)
            transport.sendMessage(mm, mm.allRecipients)
            transport.close()
            val updatedMessage = message.copy(forwarded = true)
            // Set message status as forwarded such that it is ignored
            // the next time we try to forward messages still pending
            messageDao.update(updatedMessage)
            isWorkComplete.emit(true)
            return Result.success()
        } catch (e: Exception) {
            isWorkComplete.emit(true)
            return Result.failure()
        }
    }
}