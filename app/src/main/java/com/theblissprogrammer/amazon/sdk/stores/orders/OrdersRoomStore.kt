package com.theblissprogrammer.amazon.sdk.stores.orders

import com.theblissprogrammer.amazon.sdk.common.DeferredLiveResult
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetworkAsync
import com.theblissprogrammer.amazon.sdk.extensions.coroutineRoomAsync
import com.theblissprogrammer.amazon.sdk.stores.common.insertOrUpdate
import com.theblissprogrammer.amazon.sdk.stores.orderItems.OrderItemDAO
import com.theblissprogrammer.amazon.sdk.stores.orders.models.ListOrder
import com.theblissprogrammer.amazon.sdk.stores.orders.models.Order
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderDetail
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderModels

/**
 * Created by ahmed.saad on 2018-12-19.
 * Copyright © 2018. All rights reserved.
 */
class OrdersRoomStore(val orderDao: OrderDAO?,
                      val itemDAO: OrderItemDAO?): OrdersCacheStore {

    override fun fetch(request: OrderModels.Request): LiveResult<Array<OrderDetail>> {
        val items = if (request.id != null) {
            orderDao?.fetch(id = request.id, marketplaces = request.marketplaces.toTypedArray())
        } else {
            orderDao?.fetch(
                startDate = request.startDate ?: request.lastSync,
                endDate = request.endDate,
                orderStatuses = request.orderStatuses.toTypedArray(),
                marketplaces = request.marketplaces.toTypedArray()
            )
        }

        return if (items == null) {
            LiveResult.failure(DataError.NonExistent)
        } else {
            LiveResult.success(items)
        }
    }

    override fun fetchOldestOrder(): LiveResult<Order> {
        val item = orderDao?.fetchOldestOrder()

        return if (item == null) {
            LiveResult.failure(DataError.NonExistent)
        } else {
            LiveResult.success(item)
        }
    }

    override fun createOrUpdate(request: ListOrder) {
        orderDao?.insertOrUpdate(request.order)

        if (request.buyer != null)
            orderDao?.insertOrUpdate(request.buyer)
    }

    override fun createOrUpdate(vararg listOrder: ListOrder) {
        val orders = listOrder.map { it.order }
        val buyers = listOrder.mapNotNull { it.buyer }

        orderDao?.insert(*orders.toTypedArray())
        orderDao?.insert(*buyers.toTypedArray())
    }

    override fun createOrUpdate(orders: List<Order>) {
        orderDao?.insert(*orders.toTypedArray())
    }

}