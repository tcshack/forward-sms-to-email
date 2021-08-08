package com.tcscorp.forwardsmstoemail.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_table")
data class Message(
    @PrimaryKey val id: String,
    val content: String,
    val forwarded: Boolean = false
)