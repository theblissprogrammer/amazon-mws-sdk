package com.theblissprogrammer.amazon.sdk.stores.common

import com.theblissprogrammer.amazon.sdk.common.*
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetworkAsync

/**
 * Created by ahmed.saad on 2018-12-29.
 * Copyright © 2018. All rights reserved.
 */
interface CommonStore<T, R> {
    fun fetchAsync(request: R): DeferredResult<T>
    fun fetchNextAsync(nextToken: String): DeferredResult<T> {
        return coroutineNetworkAsync <T> {
            Result.failure()
        }
    }
    fun fetch(request: R): Result<T> { return Result.failure(DataError.NonExistent) }
}

interface CommonCacheStore<T, R> {
    fun fetchAsync(request: R): DeferredLiveResult<Array<T>>
    fun createOrUpdateAsync(request: T): DeferredLiveResult<T>
    fun createOrUpdateAsync(vararg inventory: T): DeferredResult<Void>
    fun createOrUpdate(items: List<T>) {}
}

interface CommonWorkerType<T, R> {
    suspend fun fetchAsync(request: R, completion: LiveCompletionResponse<Array<T>>) {}
    fun fetch(request: R, completion: LiveResourceResponse<Array<T>>) {}
}