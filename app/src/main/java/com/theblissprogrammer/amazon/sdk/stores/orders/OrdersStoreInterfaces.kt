package com.theblissprogrammer.amazon.sdk.stores.orders

import com.theblissprogrammer.amazon.sdk.common.*
import com.theblissprogrammer.amazon.sdk.stores.orders.models.*

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
interface OrdersStore {
    fun fetch(request: OrderModels.Request, completion: SuspendCompletionResponse<List<ListOrder>>): DeferredResult<List<ListOrder>>
}

interface OrdersCacheStore {
    fun fetch(request: OrderModels.Request): DeferredLiveResult<Array<Order>>
    fun fetchOldestOrder(): DeferredLiveResult<Order>
    fun createOrUpdate(request: ListOrder): DeferredLiveResult<Order>
    fun createOrUpdate(vararg orders: ListOrder): DeferredResult<Void>
}

interface OrdersWorkerType {
    suspend fun fetch(request: OrderModels.Request, completion: LiveCompletionResponse<Array<Order>>)
    suspend fun fetchOldestOrder(completion: LiveCompletionResponse<Order>)
}