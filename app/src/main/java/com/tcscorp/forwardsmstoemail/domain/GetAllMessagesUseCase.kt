package com.tcscorp.forwardsmstoemail.domain

import com.tcscorp.forwardsmstoemail.data.Message
import com.tcscorp.forwardsmstoemail.data.MessageRepository
import com.tcscorp.forwardsmstoemail.data.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    ioDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, List<Message>>(ioDispatcher) {

    override fun execute(parameter: Unit): Flow<Result<List<Message>>> =
        messageRepository.getAllMessages()
}