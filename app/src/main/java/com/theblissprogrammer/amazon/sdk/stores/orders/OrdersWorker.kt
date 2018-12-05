package com.theblissprogrammer.amazon.sdk.stores.orders

import com.theblissprogrammer.amazon.sdk.data.SyncRoomStore
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.extensions.coroutineCompletionOnUi
import com.theblissprogrammer.amazon.sdk.stores.orders.models.ExpandedOrderType
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderModels
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
class OrdersWorker(val store: OrdersStore,
                   val cacheStore: OrdersCacheStore?,
                   val preferencesWorker: PreferencesWorkerType): OrdersWorkerType {

    override fun fetch(request: OrderModels.Request, completion: CompletionResponse<List<ExpandedOrderType>>) {
        if (request.marketplaces.isEmpty()) {
            val marketplaces = SyncRoomStore.getSellerMarketplaces(preferencesWorker)
            request.marketplaces = marketplaces ?: listOf(MarketplaceType.US)
        }

        coroutineCompletionOnUi(completion) {
            val orders = store.fetch(request).await()

            // Validate if any updates that needs to be stored
            if (orders.value == null || !orders.isSuccess) {
                return@coroutineCompletionOnUi
            }

            if (cacheStore != null)
                orders.value.forEach {
                    this.cacheStore.createOrUpdate(it).await()
                }
        }

        cacheStore?.fetch(request) {
            coroutineCompletionOnUi(completion) {
                completion(it)
            }
        }
    }

    override fun fetchOrder(by: OrderModels.SearchRequest, completion: CompletionResponse<ExpandedOrderType>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fetchOldestOrder(completion: CompletionResponse<ExpandedOrderType>) {
        if (cacheStore != null)
            coroutineCompletionOnUi(completion) {
                completion(cacheStore.fetchOldestOrder().await())
            }
    }
}