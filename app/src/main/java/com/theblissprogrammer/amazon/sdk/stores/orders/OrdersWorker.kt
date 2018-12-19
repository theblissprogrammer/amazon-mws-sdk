package com.theblissprogrammer.amazon.sdk.stores.orders

import com.theblissprogrammer.amazon.sdk.data.SyncRoomStore
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.stores.orders.models.ExpandedOrder
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderModels
import com.theblissprogrammer.amazon.sdk.common.LiveCompletionResponse
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.stores.orders.models.Order

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
class OrdersWorker(val store: OrdersStore,
                   val cacheStore: OrdersCacheStore,
                   val preferencesWorker: PreferencesWorkerType): OrdersWorkerType {


    override suspend fun fetch(request: OrderModels.Request, completion: LiveCompletionResponse<List<Order>>) {
        if (request.marketplaces.isEmpty()) {
            val marketplaces = SyncRoomStore.getSellerMarketplaces(preferencesWorker)
            request.marketplaces = marketplaces ?: listOf(MarketplaceType.US)
        }

        val cache = cacheStore.fetch(request = request).await()

        // Immediately return local response
        completion(cache)

        val response = store.fetch(request).await()

        // Validate if any updates that needs to be stored
        val orders = response.value
        if (orders == null || !response.isSuccess) {
            return
        }

        val savedElement = this.cacheStore.createOrUpdate(*orders.toTypedArray()).await()

        if (!savedElement.isSuccess) {
            LogHelper.e(messages = *arrayOf("Could not save updated orders locally" +
                    " from remote storage: ${savedElement.error?.localizedMessage ?: ""}"))
        }
    }

    override suspend fun fetchOldestOrder(completion: LiveCompletionResponse<Order>) {
        completion(cacheStore.fetchOldestOrder().await())
    }
}