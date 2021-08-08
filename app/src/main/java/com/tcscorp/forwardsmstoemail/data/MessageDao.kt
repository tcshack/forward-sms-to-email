package com.tcscorp.forwardsmstoemail.data

import androidx.room.*

@Dao
interface MessageDao {

    @Query("SELECT * FROM message_table")
    fun getAll(): List<Message>

    @Query("SELECT * FROM message_table WHERE (forwarded == :forwarded)")
    fun getPendingMessages(forwarded: Boolean = false): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Message)

    @Update
    suspend fun update(task: Message)
}