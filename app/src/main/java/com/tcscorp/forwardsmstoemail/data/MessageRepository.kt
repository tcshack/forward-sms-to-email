package com.tcscorp.forwardsmstoemail.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface MessageRepository {
    fun getAllMessages(): Flow<Result<List<Message>>>
}

class DefaultMessageRepository @Inject constructor(
    private val messageDao: MessageDao
) : MessageRepository {
    override fun getAllMessages(): Flow<Result<List<Message>>> {
        return flow {
            try {
                val messages = messageDao.getAll()
                emit(Result.Success(messages))
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }
    }
}