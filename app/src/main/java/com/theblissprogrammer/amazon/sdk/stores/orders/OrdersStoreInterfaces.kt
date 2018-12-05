package com.theblissprogrammer.amazon.sdk.stores.orders

import com.theblissprogrammer.amazon.sdk.stores.orders.models.ExpandedOrderType
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderModels
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderType
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import kotlinx.coroutines.Deferred

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
interface OrdersStore {
    fun fetch(request: OrderModels.Request): Deferred<Result<List<OrderType>>>
}

interface OrdersCacheStore {
    fun fetch(request: OrderModels.Request, completion: CompletionResponse<List<ExpandedOrderType>>)
    fun fetchOldestOrder(): Deferred<Result<ExpandedOrderType>>
    fun fetchOrder(by: OrderModels.SearchRequest): Deferred<Result<ExpandedOrderType>>
    fun createOrUpdate(request: OrderType): Deferred<Result<ExpandedOrderType>>
}

interface OrdersWorkerType {
    fun fetch(request: OrderModels.Request, completion: CompletionResponse<List<ExpandedOrderType>>)
    fun fetchOldestOrder(completion: CompletionResponse<ExpandedOrderType>)
    fun fetchOrder(by: OrderModels.SearchRequest, completion: CompletionResponse<ExpandedOrderType>)
}