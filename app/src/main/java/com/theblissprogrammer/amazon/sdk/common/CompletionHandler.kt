package com.theblissprogrammer.amazon.sdk.common

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

typealias CompletionResponse<T> = (Result<T>) -> Unit
typealias DeferredResult<T> = (Deferred<Result<T>>) -> Unit
