package com.theblissprogrammer.amazon.sdk.stores.orders

import com.theblissprogrammer.amazon.sdk.common.DeferredLiveResult
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.enums.OrderStatus
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetwork
import com.theblissprogrammer.amazon.sdk.extensions.coroutineRoom
import com.theblissprogrammer.amazon.sdk.stores.orders.models.ExpandedOrder
import com.theblissprogrammer.amazon.sdk.stores.orders.models.ListOrder
import com.theblissprogrammer.amazon.sdk.stores.orders.models.Order
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderModels
import com.theblissprogrammer.amazon.sdk.stores.sellers.insertOrUpdate

/**
 * Created by ahmed.saad on 2018-12-19.
 * Copyright © 2018. All rights reserved.
 */
class OrdersRoomStore(val orderDao: OrderDAO?): OrdersCacheStore {

    override fun fetch(request: OrderModels.Request): DeferredLiveResult<Array<Order>> {
        return coroutineRoom<Array<Order>> {

            val items = if (request.id != null) {
                orderDao?.fetch(id = request.id, marketplaces = request.marketplaces.toTypedArray())
            } else {
                orderDao?.fetch(
                    startDate = request.startDate,
                    endDate = request.endDate,
                    orderStatuses = request.orderStatuses.toTypedArray(),
                    marketplaces = request.marketplaces.toTypedArray()
                )
            }

            if (items == null) {
                LiveResult.failure(DataError.NonExistent)
            } else {
                LiveResult.success(items)
            }
        }
    }

    override fun fetchOldestOrder(): DeferredLiveResult<Order> {
        return coroutineRoom<Order> {
            val item = orderDao?.fetchOldestOrder()

            if (item == null) {
                LiveResult.failure(DataError.NonExistent)
            } else {
                LiveResult.success(item)
            }
        }
    }

    override fun createOrUpdate(request: ListOrder): DeferredLiveResult<Order> {
        return coroutineRoom<Order> {

            orderDao?.insertOrUpdate(request.order)

            if (request.buyer != null)
                orderDao?.insertOrUpdate(request.buyer)

            val marketplace = request.order.marketplace

            val item = if (marketplace != null) {
                orderDao?.fetch(id = request.order.id, marketplace = marketplace)
            } else {
                orderDao?.fetch(id = request.order.id)
            }

            if (item == null) {
                LiveResult.failure(DataError.NonExistent)
            } else {
                LiveResult.success(item)
            }
        }
    }

    override fun createOrUpdate(vararg listOrder: ListOrder): DeferredResult<Void> {
        return coroutineNetwork<Void> {
            val orders = listOrder.map { it.order }
            val buyers = listOrder.mapNotNull { it.buyer }

            orderDao?.insert(*orders.toTypedArray())
            orderDao?.insert(*buyers.toTypedArray())
            Result.success()
        }
    }

}