package com.theblissprogrammer.amazon.sdk.stores.inventory

import com.theblissprogrammer.amazon.sdk.common.DeferredLiveResult
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetworkAsync
import com.theblissprogrammer.amazon.sdk.extensions.coroutineRoomAsync
import com.theblissprogrammer.amazon.sdk.stores.common.insertOrUpdate
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.InventoryDetail
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.InventoryModels

/**
 * Created by ahmed.saad on 2018-12-19.
 * Copyright © 2018. All rights reserved.
 */
class InventoryRoomStore(val inventoryDao: InventoryDAO?): InventoryCacheStore {

    override fun fetch(request: InventoryModels.Request): LiveResult<Array<InventoryDetail>> {
        val items = if (request.skus.isNotEmpty())
            inventoryDao?.fetch(request.skus.toTypedArray())
        else
            inventoryDao?.fetch(request.marketplace ?: MarketplaceType.US)

        return if (items == null) {
            LiveResult.failure(DataError.NonExistent)
        } else {
            LiveResult.success(items)
        }
    }

    override fun createOrUpdateAsync(request: Inventory): DeferredLiveResult<Inventory> {
        return coroutineRoomAsync<Inventory> {

            inventoryDao?.insertOrUpdate(request)

            val item = inventoryDao?.fetch(sku = request.sku)

            if (item == null) {
                LiveResult.failure(DataError.NonExistent)
            } else {
                LiveResult.success(item)
            }
        }
    }

    override fun createOrUpdateAsync(vararg inventory: Inventory): DeferredResult<Void> {
        return coroutineNetworkAsync<Void> {
            inventoryDao?.insert(*inventory)
            Result.success()
        }
    }

    override fun createOrUpdate(items: List<Inventory>) {
        inventoryDao?.insert(*items.toTypedArray())
    }

}