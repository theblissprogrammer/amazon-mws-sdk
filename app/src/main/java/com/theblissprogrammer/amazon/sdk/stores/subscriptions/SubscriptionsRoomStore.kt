package com.theblissprogrammer.amazon.sdk.stores.subscriptions

import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.stores.common.insertOrUpdate
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.Queue
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.SubscriptionsModels

/**
 * Created by ahmed.saad on 2019-09-26.
 * Copyright © 2019. All rights reserved.
 */
class SubscriptionsRoomStore(val subscriptionsDao: SubscriptionsDAO?): SubscriptionsCacheStore {

    override fun getQueue(request: SubscriptionsModels.QueueRequest): LiveResult<Queue> {
        val item = subscriptionsDao?.fetchQueue(name = request.name, marketplace = request.marketplace)

        return if (item == null) {
            LiveResult.failure(DataError.NonExistent)
        } else {
            LiveResult.success(item)
        }
    }

    override fun createOrUpdateQueue(request: Queue) {
        subscriptionsDao?.insertOrUpdate(request)
    }
}