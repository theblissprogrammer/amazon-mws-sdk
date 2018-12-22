package com.theblissprogrammer.amazon.sdk.stores.orderItems

import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.DeferredLiveResult
import com.theblissprogrammer.amazon.sdk.common.LiveCompletionResponse
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.ListOrderItems
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.OrderItem
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.OrderItemModels

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
interface OrderItemsStore {
    fun fetch(id: String): DeferredResult<ListOrderItems>
    fun fetchNext(nextToken: String): DeferredResult<ListOrderItems>
}

interface OrderItemsCacheStore {
    fun fetch(request: OrderItemModels.Request): DeferredLiveResult<Array<OrderItem>>
    fun createOrUpdate(request: OrderItem): DeferredLiveResult<OrderItem>
    fun createOrUpdate(vararg orderItem: OrderItem): DeferredResult<Void>
}

interface OrderItemsWorkerType {
    suspend fun fetch(request: OrderItemModels.Request, completion: LiveCompletionResponse<Array<OrderItem>>)
}