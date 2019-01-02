package com.theblissprogrammer.amazon.sdk.stores.orders

import com.theblissprogrammer.amazon.sdk.common.*
import com.theblissprogrammer.amazon.sdk.stores.common.CommonCacheStore
import com.theblissprogrammer.amazon.sdk.stores.common.CommonStore
import com.theblissprogrammer.amazon.sdk.stores.common.CommonWorkerType
import com.theblissprogrammer.amazon.sdk.stores.orders.models.*

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
interface OrdersStore: CommonStore<ListOrders, OrderModels.Request>

interface OrdersCacheStore {
    fun fetch(request: OrderModels.Request): DeferredLiveResult<Array<Order>>
    fun fetchOldestOrder(): DeferredLiveResult<Order>
    fun createOrUpdate(request: ListOrder): DeferredLiveResult<Order>
    fun createOrUpdate(vararg listOrder: ListOrder): DeferredResult<Void>
}

interface OrdersWorkerType: CommonWorkerType<Order, OrderModels.Request> {
    suspend fun fetchOldestOrder(completion: LiveCompletionResponse<Order>)
}