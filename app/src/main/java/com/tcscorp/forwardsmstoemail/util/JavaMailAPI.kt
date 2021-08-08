package com.tcscorp.forwardsmstoemail.util

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


open class JavaMailAPI(
    private val mContext: Context,
    private val mEmail: String,
    private val mSubject: String,
    private val mMessage: String
) : AsyncTask<Void?, Void?, Void?>() {
    private var mSession: Session? = null
    private var mProgressDialog: ProgressDialog? = null
    override fun onPreExecute() {
        super.onPreExecute()
        mProgressDialog =
            ProgressDialog.show(mContext, "Sending message", "Please wait...", false, false)
    }

    override fun onPostExecute(aVoid: Void?) {
        super.onPostExecute(aVoid)
        //Dismiss progress dialog when message successfully send
        mProgressDialog!!.dismiss()

        //Show success toast
        Toast.makeText(mContext, "Message Sent", Toast.LENGTH_SHORT).show()
    }

    override fun doInBackground(vararg params: Void?): Void? {
        //Creating properties
        val props = Properties()

        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.socketFactory.port"] = "465"
        props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.port"] = "465"

        mSession = Session.getDefaultInstance(props,
            object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication("Config.EMAIL", "Config.PASSWORD")
                }
            })
        try {
            val mm = MimeMessage(mSession)
            mm.setFrom(InternetAddress("Config.EMAIL"))
            mm.addRecipient(Message.RecipientType.TO, InternetAddress(mEmail))
            mm.subject = mSubject
            mm.setText(mMessage)
            Transport.send(mm)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
        return null
    }
}