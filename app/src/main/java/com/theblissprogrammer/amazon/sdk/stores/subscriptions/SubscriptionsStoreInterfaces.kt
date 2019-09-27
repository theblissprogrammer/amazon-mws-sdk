package com.theblissprogrammer.amazon.sdk.stores.subscriptions

import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.common.DeferredLiveResult
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.LiveCompletionResponse
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.Queue
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.SubscriptionsModels

/**
 * Created by ahmed.saad on 2019-09-26.
 * Copyright © 2019. All rights reserved.
 */

interface SubscriptionsStore {
    fun getQueue(request: SubscriptionsModels.Request): DeferredResult<Queue>
}

interface SubscriptionsCacheStore {
    fun getQueue(request: SubscriptionsModels.Request): DeferredLiveResult<Queue>
    fun createOrUpdateQueue(request: Queue): DeferredLiveResult<Queue>
}

interface SubscriptionsWorkerType {
    suspend fun getQueue(completion: LiveCompletionResponse<Queue>)
}