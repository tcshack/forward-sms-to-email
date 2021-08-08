package com.tcscorp.forwardsmstoemail.ui

open class UiState<T>(
    var isLoading: Boolean = false,
    var data: T? = null,
    var error: Throwable? = null
)

inline fun <reified T> UiState<T>.doOnError(callback: (throwable: Throwable?) -> Unit) {
    if (error != null) {
        callback(error)
    }
}

inline fun <reified T> UiState<T>.doOnProgress(callback: () -> Unit) {
    if (isLoading) {
        callback()
    }
}

inline fun <reified T> UiState<T>.doOnSuccess(callback: (value: T) -> Unit) {
    if (data != null) {
        callback(data!!)
    }
}