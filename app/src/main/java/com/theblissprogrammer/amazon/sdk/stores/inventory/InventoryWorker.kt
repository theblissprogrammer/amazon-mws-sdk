package com.theblissprogrammer.amazon.sdk.stores.inventory

import com.theblissprogrammer.amazon.sdk.stores.inventory.models.InventoryModels
import com.theblissprogrammer.amazon.sdk.common.LiveResourceResponse
import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.extensions.coroutineBackgroundAsync
import com.theblissprogrammer.amazon.sdk.extensions.coroutineOnIO
import com.theblissprogrammer.amazon.sdk.network.NetworkBoundResource
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.InventoryDetail
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.ListInventorySupply

/**
 * Created by ahmedsaad on 2018-08-07.
 * Copyright (c) 2018. All rights reserved.
 **/
class InventoryWorker(val store: InventoryStore,
                      val cacheStore: InventoryCacheStore,
                      val preferencesWorker: PreferencesWorkerType): InventoryWorkerType {

    override fun fetch(request: InventoryModels.Request, completion: LiveResourceResponse<Array<InventoryDetail>>) {
        if (request.marketplace == null) {
            request.marketplace =  MarketplaceType.valueOf(preferencesWorker.get(DefaultsKeys.marketplace) ?: "US")
        }

        val data = object : NetworkBoundResource<Array<InventoryDetail>, ListInventorySupply>() {
            override fun saveCallResult(item: ListInventorySupply?) {
                if (item != null) {
                    val inventories = item.inventory
                    inventories.forEach { inventory -> inventory.marketplace = item.marketplace }

                    cacheStore.createOrUpdate(inventories)
                }
            }

            override fun shouldFetch(data: Array<InventoryDetail>?): Boolean {
                return false
            }

            override fun loadFromDb(): LiveResult<Array<InventoryDetail>> {
                return cacheStore.fetch(request = request)
            }

            override fun createCall(): Result<ListInventorySupply> {
                return store.fetch(request = request)
            }

        }.asLiveData()

        completion(data)
    }

    override fun update(request: InventoryModels.Request) {
        coroutineOnIO {
            val data = coroutineBackgroundAsync {
                store.fetch(request = request)
            }.await()

            val value = data.value
            if (value != null) {
                val inventories = value.inventory
                inventories.forEach { inventory -> inventory.marketplace = value.marketplace }

                cacheStore.createOrUpdate(inventories)
            }
        }
    }

    /*override suspend fun fetchAsync(request: InventoryModels.Request, completion: LiveCompletionResponse<Array<Inventory>>) {
        if (request.marketplace == null) {
            request.marketplace = marketplaceFromId(preferencesWorker.get(DefaultsKeys.marketplace)) ?: MarketplaceType.US
        }

        val cache = cacheStore.fetchAsync(request = request).await()

        // Immediately return local response
        // completion(cache)

        val listInventorySupply = store.fetchAsync(request).await()

        if (!listInventorySupply.isSuccess || listInventorySupply.value == null) {
            return LogHelper.e(messages = *arrayOf("Error occurred while retrieving inventory : ${listInventorySupply.error ?: ""}"))
        }

        val inventories = listInventorySupply.value.inventory
        inventories.forEach { inventory -> inventory.marketplace = listInventorySupply.value.marketplace }

        val savedElement = this.cacheStore.createOrUpdateAsync(*inventories.toTypedArray()).await()

        if (!savedElement.isSuccess) {
            return LogHelper.e(
                messages = *arrayOf(
                    "Could not save updated inventory locally" +
                            " from remote storage: ${savedElement.error?.localizedMessage ?: ""}"
                )
            )
        }

        var nextToken = listInventorySupply.value.nextToken
        while (nextToken != null) {

            val listInventorySupplyNext = store.fetchNextAsync(nextToken = nextToken).await()

            if (!listInventorySupplyNext.isSuccess || listInventorySupplyNext.value == null) {
                return LogHelper.e(messages = *arrayOf("Error occurred while retrieving inventory next : ${listInventorySupplyNext.error ?: ""}"))
            }

            val inventoriesNext = listInventorySupplyNext.value.inventory
            inventoriesNext.forEach { inventory -> inventory.marketplace = listInventorySupplyNext.value.marketplace }

            val savedElementNext = this.cacheStore.createOrUpdateAsync(*inventoriesNext.toTypedArray()).await()

            if (!savedElementNext.isSuccess) {
                return LogHelper.e(
                    messages = *arrayOf(
                        "Could not save updated inventory locally" +
                                " from remote storage: ${savedElementNext.error?.localizedMessage ?: ""}"
                    )
                )
            }

            nextToken = listInventorySupplyNext.value.nextToken
        }

        completion(cache)
    }*/
}