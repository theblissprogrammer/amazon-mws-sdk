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
    fun fetch(request: OrderModels.Request): LiveResult<Array<OrderDetail>>
    fun fetchOldestOrder(): LiveResult<Order>
    fun createOrUpdate(request: ListOrder)
    fun createOrUpdate(vararg listOrder: ListOrder)
    fun createOrUpdate(orders: List<Order>)
}

interface OrdersWorkerType: CommonWorkerType<OrderDetail, OrderModels.Request> {
    suspend fun fetchOldestOrder(completion: LiveCompletionResponse<Order>)
}