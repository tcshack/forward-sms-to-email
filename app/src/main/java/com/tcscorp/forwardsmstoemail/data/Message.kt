package com.tcscorp.forwardsmstoemail.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_table")
data class Message(
    val address: String,
    val body: String,
    val dateSent: Long,
    val date: Long = System.currentTimeMillis(),
    val forwarded: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Long = date
)