package com.theblissprogrammer.amazon.sdk.stores.inventory

import com.theblissprogrammer.amazon.sdk.common.*
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.InventoryModels
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.ListInventorySupply

/**
 * Created by ahmedsaad on 2018-08-07.
 * Copyright (c) 2018. All rights reserved.
 **/
interface InventoryStore {
    fun fetch(request: InventoryModels.Request): DeferredResult<ListInventorySupply>
    fun fetchNext(nextToken: String): DeferredResult<ListInventorySupply>
}

interface InventoryCacheStore {
    fun fetch(request: InventoryModels.Request): DeferredLiveResult<Array<Inventory>>
    fun createOrUpdate(request: Inventory): DeferredLiveResult<Inventory>
    fun createOrUpdate(vararg inventory: Inventory): DeferredResult<Void>
}

interface InventoryWorkerType {
    suspend fun fetch(request: InventoryModels.Request, completion: LiveCompletionResponse<Array<Inventory>>)
}