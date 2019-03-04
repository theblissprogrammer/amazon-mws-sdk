package com.theblissprogrammer.amazon.sdk.stores.inventory

import com.theblissprogrammer.amazon.sdk.stores.inventory.models.InventoryModels
import com.theblissprogrammer.amazon.sdk.common.LiveCompletionResponse
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory

/**
 * Created by ahmedsaad on 2018-08-07.
 * Copyright (c) 2018. All rights reserved.
 **/
class InventoryWorker(val store: InventoryStore,
                      val cacheStore: InventoryCacheStore,
                      val preferencesWorker: PreferencesWorkerType): InventoryWorkerType {

    override suspend fun fetch(request: InventoryModels.Request, completion: LiveCompletionResponse<Array<Inventory>>) {
        if (request.marketplace == null) {
            request.marketplace = MarketplaceType.valueOf(preferencesWorker.get(DefaultsKeys.marketplace) ?: "US")
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
    }
}