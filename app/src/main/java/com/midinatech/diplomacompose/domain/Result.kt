package com.midinatech.diplomacompose.domain

sealed interface Result<T> {
    data class Success<T>(val data: T) : Result<T>
    data class Failure<T>(val failure: Throwable) : Result<T>
    data class Cancelled<T>(val message: String) : Result<T>

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun <T> failure(failure: Throwable): Result<T> = Failure(failure)
        fun <T> cancelled(message: String): Result<T> = Cancelled(message)
    }
}