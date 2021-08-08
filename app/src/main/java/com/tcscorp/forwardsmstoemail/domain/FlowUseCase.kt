package com.tcscorp.forwardsmstoemail.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import com.tcscorp.forwardsmstoemail.data.Result

/**
 * Executes business logic in its execute method and keep posting updates to the result as
 * [Result<R>].
 * Handling an exception (emit [Result.Error] to the result) is the subclasses's responsibility.
 */
abstract class FlowUseCase<T, R>(private val coroutineDispatcher: CoroutineDispatcher) {
    operator fun invoke(parameter: T): Flow<Result<R>> = execute(parameter)
        .catch { t -> emit(Result.Error(Throwable(t))) }
        .flowOn(coroutineDispatcher)

    protected abstract fun execute(parameter: T): Flow<Result<R>>
}