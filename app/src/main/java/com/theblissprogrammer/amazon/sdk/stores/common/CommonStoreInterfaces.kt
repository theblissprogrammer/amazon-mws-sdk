package com.theblissprogrammer.amazon.sdk.stores.common

import com.theblissprogrammer.amazon.sdk.common.DeferredLiveResult
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.LiveCompletionResponse

/**
 * Created by ahmed.saad on 2018-12-29.
 * Copyright © 2018. All rights reserved.
 */
interface CommonStore<T, R> {
    fun fetch(request: R): DeferredResult<T>
    fun fetchNext(nextToken: String): DeferredResult<T>
}

interface CommonCacheStore<T, R> {
    fun fetch(request: R): DeferredLiveResult<Array<T>>
    fun createOrUpdate(request: T): DeferredLiveResult<T>
    fun createOrUpdate(vararg inventory: T): DeferredResult<Void>
}

interface CommonWorkerType<T, R> {
    suspend fun fetch(request: R, completion: LiveCompletionResponse<Array<T>>)
}