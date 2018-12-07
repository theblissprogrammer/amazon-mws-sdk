package com.theblissprogrammer.amazon.sdk.common

import androidx.lifecycle.LiveData
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.errors.NetworkError
import kotlinx.coroutines.Deferred

/**
* Created by ahmedsaad on 2017-10-24.
* Copyright © 2017. All rights reserved.
*/

data class Result<T>(val isSuccess: Boolean = false, val value: T? = null, val error: DataError? = null) {
    companion object {
        fun <T>success(value: T? = null): Result<T> {
            return Result(true, value, null)
        }

        fun <T>failure(error: DataError? = null): Result<T> {
            return Result(false, null, error)
        }
    }
}

data class NetworkResult<T>(val isSuccess: Boolean = false, val value: T? = null, val error: NetworkError? = null) {
    companion object {
        fun <T>success(value: T? = null): NetworkResult<T> {
            return NetworkResult(true, value, null)
        }

        fun <T>failure(error: NetworkError? = null): NetworkResult<T> {
            return NetworkResult(false, null, error)
        }
    }
}

data class LiveResult<T>(val isSuccess: Boolean = false, val value: LiveData<T>? = null, val error: DataError? = null) {
    companion object {
        fun <T>success(value: LiveData<T>? = null): LiveResult<T> {
            return LiveResult(true, value, null)
        }

        fun <T>failure(error: DataError? = null): LiveResult<T> {
            return LiveResult(false, null, error)
        }
    }
}

typealias CompletionResponse<T> = (Result<T>) -> Unit
typealias LiveCompletionResponse<T> = (LiveResult<T>) -> Unit
typealias DeferredLiveResult<T> = Deferred<LiveResult<T>>
typealias DeferredResult<T> = Deferred<Result<T>>
