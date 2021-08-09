package com.tcscorp.forwardsmstoemail.data

import androidx.room.*

@Dao
interface MessageDao {

    @Query("SELECT * FROM message_table")
    fun getAll(): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Message): Long

    @Update
    suspend fun update(task: Message)
}