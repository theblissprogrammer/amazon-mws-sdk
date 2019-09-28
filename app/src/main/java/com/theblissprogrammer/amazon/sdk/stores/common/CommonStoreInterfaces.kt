package com.theblissprogrammer.amazon.sdk.stores.common

import com.theblissprogrammer.amazon.sdk.common.DeferredLiveResult
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.LiveCompletionResponse
import com.theblissprogrammer.amazon.sdk.common.Result
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
}

interface CommonCacheStore<T, R> {
    fun fetchAsync(request: R): DeferredLiveResult<Array<T>>
    fun createOrUpdateAsync(request: T): DeferredLiveResult<T>
    fun createOrUpdateAsync(vararg inventory: T): DeferredResult<Void>
}

interface CommonWorkerType<T, R> {
    suspend fun fetch(request: R, completion: LiveCompletionResponse<Array<T>>)
}