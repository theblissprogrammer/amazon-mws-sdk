package com.theblissprogrammer.amazon.sdk.stores.inventories

import com.theblissprogrammer.amazon.sdk.stores.inventories.models.InventoryModels
import com.theblissprogrammer.amazon.sdk.stores.inventories.models.InventoryType
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import kotlinx.coroutines.Deferred

/**
 * Created by ahmedsaad on 2018-08-07.
 * Copyright (c) 2018. All rights reserved.
 **/
interface InventoriesStore {
    fun fetch(request: InventoryModels.Request): Deferred<Result<List<InventoryType>>>
}

interface InventoriesCacheStore {
    fun fetch(request: InventoryModels.Request): Deferred<Result<List<InventoryType>>>
    fun createOrUpdate(request: InventoryType): Deferred<Result<InventoryType>>
}

interface InventoriesWorkerType {
    fun fetch(request: InventoryModels.Request, completion: CompletionResponse<List<InventoryType>>)
}