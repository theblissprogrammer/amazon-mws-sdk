package com.theblissprogrammer.amazon.sdk.stores.orders

import com.theblissprogrammer.amazon.sdk.data.SyncRoomStore
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.common.LiveCompletionResponse
import com.theblissprogrammer.amazon.sdk.common.LiveResourceResponse
import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.extensions.coroutineOnUi
import com.theblissprogrammer.amazon.sdk.extensions.coroutineRoomAsync
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.network.NetworkBoundResource
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.stores.orders.models.*

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
class OrdersWorker(val store: OrdersStore,
                   val cacheStore: OrdersCacheStore,
                   val preferencesWorker: PreferencesWorkerType): OrdersWorkerType {

    override fun fetch(request: OrderModels.Request, completion: LiveResourceResponse<Array<OrderDetail>>) {

        if (request.marketplaces.isNullOrEmpty()) {
            val marketplaces = SyncRoomStore.getSellerMarketplaces(preferencesWorker)
            request.marketplaces = marketplaces
        }

        val data = object : NetworkBoundResource<Array<OrderDetail>, ListOrders>() {
            override fun saveCallResult(item: ListOrders?) {
                if (item != null) {
                    cacheStore.createOrUpdate(*item.orders.toTypedArray())
                }
            }

            override fun shouldFetch(data: Array<OrderDetail>?): Boolean {
                return false
            }

            override fun loadFromDb(): LiveResult<Array<OrderDetail>> {
                return cacheStore.fetch(request = request)
            }

            override fun createCall(): Result<ListOrders> {
                return store.fetch(request = request)
            }

        }.asLiveData()

        completion(data)

        /*val cache = cacheStore.fetch(request = request).await()

        // Immediately return local response
        //completion(cache)

        val listOrder = store.fetchAsync(request = request).await()

        if (!listOrder.isSuccess || listOrder.value == null) {
           return LogHelper.e(messages = *arrayOf("Error occurred while retrieving orders : ${listOrder.error ?: ""}"))
        }

        val orders = listOrder.value.orders

        val savedElement = this.cacheStore.createOrUpdate(*orders.toTypedArray()).await()

        if (!savedElement.isSuccess) {
            return LogHelper.e(
                messages = *arrayOf(
                    "Could not save updated orders locally" +
                            " from remote storage: ${savedElement.error?.localizedMessage ?: ""}"
                )
            )
        }

        var nextToken = listOrder.value.nextToken
        while (nextToken != null) {

            val listOrderNext = store.fetchNextAsync(nextToken = nextToken).await()

            if (!listOrderNext.isSuccess || listOrderNext.value == null) {
                return LogHelper.e(messages = *arrayOf("Error occurred while retrieving orders next : ${listOrderNext.error ?: ""}"))
            }

            val ordersNext = listOrderNext.value.orders

            val savedElementNext = this.cacheStore.createOrUpdate(*ordersNext.toTypedArray()).await()

            if (!savedElementNext.isSuccess) {
                return LogHelper.e(
                    messages = *arrayOf(
                        "Could not save updated orders locally" +
                                " from remote storage: ${savedElementNext.error?.localizedMessage ?: ""}"
                    )
                )
            }

            nextToken = listOrderNext.value.nextToken
        }

        completion(cache)*/
    }

    override suspend fun fetchOldestOrder(completion: LiveCompletionResponse<Order>) {
        coroutineOnUi {
            val data = coroutineRoomAsync {
                cacheStore.fetchOldestOrder()
            }.await()

            completion(data)
        }
    }
}