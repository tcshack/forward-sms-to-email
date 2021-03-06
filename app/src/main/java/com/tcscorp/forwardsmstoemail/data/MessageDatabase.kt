package com.tcscorp.forwardsmstoemail.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Message::class], version = 6)
abstract class MessageDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}