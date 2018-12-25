package com.theblissprogrammer.amazon.sdk.stores.orderItems

import com.theblissprogrammer.amazon.sdk.common.LiveCompletionResponse
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.OrderItem
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.OrderItemModels

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
class OrderItemsWorker(val store: OrderItemsStore,
                       val cacheStore: OrderItemsCacheStore): OrderItemsWorkerType {


    override suspend fun fetch(request: OrderItemModels.Request, completion: LiveCompletionResponse<Array<OrderItem>>) {

        val cache = cacheStore.fetch(request = request).await()

        // Immediately return local response
        completion(cache)

        request.ids.forEach {
            val listOrderItems = store.fetch(id = it).await()

            if (!listOrderItems.isSuccess || listOrderItems.value == null) {
                return LogHelper.e(messages = *arrayOf("Error occurred while retrieving orders : ${listOrderItems.error ?: ""}"))
            }

            val orderItems = listOrderItems.value.orderItems
            orderItems.forEach { orderItem -> orderItem.orderId = listOrderItems.value.orderId ?: "" }

            val savedElement = this.cacheStore.createOrUpdate(*orderItems.toTypedArray()).await()

            if (!savedElement.isSuccess) {
                return LogHelper.e(
                    messages = *arrayOf(
                        "Could not save updated order items locally" +
                                " from remote storage: ${savedElement.error?.localizedMessage ?: ""}"
                    )
                )
            }

            var nextToken = listOrderItems.value.nextToken
            while (nextToken != null) {

                val listOrderItemsNext = store.fetchNext(nextToken = nextToken).await()

                if (!listOrderItemsNext.isSuccess || listOrderItemsNext.value == null) {
                    return LogHelper.e(messages = *arrayOf("Error occurred while retrieving order items next : ${listOrderItemsNext.error ?: ""}"))
                }

                val orderItemsNext = listOrderItemsNext.value.orderItems
                orderItemsNext.forEach { orderItem -> orderItem.orderId = listOrderItemsNext.value.orderId ?: "" }

                val savedElementNext = this.cacheStore.createOrUpdate(*orderItemsNext.toTypedArray()).await()

                if (!savedElementNext.isSuccess) {
                    return LogHelper.e(
                        messages = *arrayOf(
                            "Could not save updated order items locally" +
                                    " from remote storage: ${savedElementNext.error?.localizedMessage ?: ""}"
                        )
                    )
                }

                nextToken = listOrderItemsNext.value.nextToken
            }
        }

        completion(cache)
    }
}