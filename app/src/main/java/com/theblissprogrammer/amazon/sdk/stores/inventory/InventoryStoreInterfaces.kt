package com.theblissprogrammer.amazon.sdk.stores.inventory

import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.stores.common.CommonCacheStore
import com.theblissprogrammer.amazon.sdk.stores.common.CommonStore
import com.theblissprogrammer.amazon.sdk.stores.common.CommonWorkerType
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.InventoryModels
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.InventoryDetail
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.ListInventorySupply

/**
 * Created by ahmedsaad on 2018-08-07.
 * Copyright (c) 2018. All rights reserved.
 **/
interface InventoryStore: CommonStore<ListInventorySupply, InventoryModels.Request>

interface InventoryCacheStore: CommonCacheStore<Inventory, InventoryModels.Request> {
    fun fetch(request: InventoryModels.Request): LiveResult<Array<InventoryDetail>>
}

interface InventoryWorkerType: CommonWorkerType<InventoryDetail, InventoryModels.Request>