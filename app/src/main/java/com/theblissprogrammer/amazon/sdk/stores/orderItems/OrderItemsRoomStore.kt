package com.theblissprogrammer.amazon.sdk.stores.orderItems

import com.theblissprogrammer.amazon.sdk.common.DeferredLiveResult
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetwork
import com.theblissprogrammer.amazon.sdk.extensions.coroutineRoom
import com.theblissprogrammer.amazon.sdk.stores.common.insertOrUpdate
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.OrderItem
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.OrderItemModels

/**
 * Created by ahmed.saad on 2018-12-19.
 * Copyright © 2018. All rights reserved.
 */
class OrderItemsRoomStore(val orderItemDao: OrderItemDAO?): OrderItemsCacheStore {

    override fun fetchAsync(request: OrderItemModels.Request): DeferredLiveResult<Array<OrderItem>> {
        return coroutineRoom<Array<OrderItem>> {

            val items = orderItemDao?.fetchByOrderId(request.ids.toTypedArray())

            if (items == null) {
                LiveResult.failure(DataError.NonExistent)
            } else {
                LiveResult.success(items)
            }
        }
    }

    override fun createOrUpdateAsync(request: OrderItem): DeferredLiveResult<OrderItem> {
        return coroutineRoom<OrderItem> {

            orderItemDao?.insertOrUpdate(request)

            val item = orderItemDao?.fetch(id = request.orderItemId)

            if (item == null) {
                LiveResult.failure(DataError.NonExistent)
            } else {
                LiveResult.success(item)
            }
        }
    }

    override fun createOrUpdateAsync(vararg orderItem: OrderItem): DeferredResult<Void> {
        return coroutineNetwork<Void> {
            orderItemDao?.insert(*orderItem)
            Result.success()
        }
    }

}