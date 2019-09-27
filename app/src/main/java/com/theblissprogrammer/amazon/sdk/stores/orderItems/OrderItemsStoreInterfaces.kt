package com.theblissprogrammer.amazon.sdk.stores.orderItems

import com.theblissprogrammer.amazon.sdk.common.LiveCompletionResponse
import com.theblissprogrammer.amazon.sdk.stores.common.CommonCacheStore
import com.theblissprogrammer.amazon.sdk.stores.common.CommonStore
import com.theblissprogrammer.amazon.sdk.stores.common.CommonWorkerType
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.ListOrderItems
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.OrderItem
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.OrderItemModels
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderModels

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
interface OrderItemsStore: CommonStore<ListOrderItems, String>

interface OrderItemsCacheStore: CommonCacheStore<OrderItem, OrderItemModels.Request>

interface OrderItemsWorkerType: CommonWorkerType<OrderItem, OrderItemModels.Request>