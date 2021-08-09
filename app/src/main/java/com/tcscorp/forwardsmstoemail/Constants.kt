package com.tcscorp.forwardsmstoemail


@JvmField
val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Verbose WorkManager Notifications"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notifications whenever work starts"
@JvmField
val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
const val NOTIFICATION_ID = 1
const val KEY_SENDER_ADDRESS = "sender_address"
const val KEY_MESSAGE_BODY = "message_body"
const val KEY_DATE_SENT = "message_date_sent"
const val RECIPIENT = "tcheuffayvan@yahoo.com"
const val SUBJECT = "SMS Forwarder"
const val FROM = "******" // Enter the full email address here e.g: john.doe@gmail.com
const val USER = "******" // Enter only the username part of the email address here e.g: john.doe
const val PASSWORD = "****"
const val SEND_EMAIL_WORK = "send_email"
const val PROTOCOL = "smtp"
const val HOST = "smtp.gmail.com"
const val PORT = "587"
const val FORWARD_SMS = "forward_sms"