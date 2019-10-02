package com.theblissprogrammer.amazon.sdk.stores.subscriptions

import com.theblissprogrammer.amazon.sdk.common.*
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.Notification
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.Queue
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.SubscriptionsModels

/**
 * Created by ahmed.saad on 2019-09-26.
 * Copyright © 2019. All rights reserved.
 */

interface SubscriptionsStore {
    fun getQueue(request: SubscriptionsModels.QueueRequest): Result<Queue>
    fun registerDestination(request: SubscriptionsModels.DestinationRequest): Result<Void>
    fun createSubscription(request: SubscriptionsModels.SubscriptionRequest): Result<Void>
    fun pollQueue(request: SubscriptionsModels.PollRequest): Result<List<Notification<Any>>>
}

interface SubscriptionsCacheStore {
    fun getQueue(request: SubscriptionsModels.QueueRequest): LiveResult<Queue>
    fun createOrUpdateQueue(request: Queue)
}

interface SubscriptionsWorkerType {
    fun getQueue(completion: LiveResourceResponse<Queue>)
    fun registerDestination(request: SubscriptionsModels.DestinationRequest, completion: ResourceResponse<Void>)
    fun createSubscription(request: SubscriptionsModels.SubscriptionRequest, completion: ResourceResponse<Void>)
    fun pollQueue(request: SubscriptionsModels.PollRequest)
}