package com.lfssolutions.retialtouch.domain

sealed class ResultDBState<out T> {
    data object Loading : ResultDBState<Nothing>()
    data class Success<out T>(val data: T) : ResultDBState<T>()
    data class Error(val message: String) : ResultDBState<Nothing>()
}