package com.theblissprogrammer.amazon.sdk.stores.inventory

import com.theblissprogrammer.amazon.sdk.common.DeferredLiveResult
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetwork
import com.theblissprogrammer.amazon.sdk.extensions.coroutineRoom
import com.theblissprogrammer.amazon.sdk.stores.common.insertOrUpdate
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.InventoryModels

/**
 * Created by ahmed.saad on 2018-12-19.
 * Copyright © 2018. All rights reserved.
 */
class InventoryRoomStore(val inventoryDao: InventoryDAO?): InventoryCacheStore {

    override fun fetch(request: InventoryModels.Request): DeferredLiveResult<Array<Inventory>> {
        return coroutineRoom<Array<Inventory>> {

            val items = if (request.skus.isNotEmpty())
                inventoryDao?.fetch(request.skus.toTypedArray())
            else
                inventoryDao?.fetch(request.marketplace ?: MarketplaceType.US)

            if (items == null) {
                LiveResult.failure(DataError.NonExistent)
            } else {
                LiveResult.success(items)
            }
        }
    }

    override fun createOrUpdate(request: Inventory): DeferredLiveResult<Inventory> {
        return coroutineRoom<Inventory> {

            inventoryDao?.insertOrUpdate(request)

            val item = inventoryDao?.fetch(sku = request.sku)

            if (item == null) {
                LiveResult.failure(DataError.NonExistent)
            } else {
                LiveResult.success(item)
            }
        }
    }

    override fun createOrUpdate(vararg inventory: Inventory): DeferredResult<Void> {
        return coroutineNetwork<Void> {
            inventoryDao?.insert(*inventory)
            Result.success()
        }
    }

}