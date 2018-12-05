package com.theblissprogrammer.amazon.sdk.stores.inventories

import com.theblissprogrammer.amazon.sdk.stores.inventories.models.InventoryModels
import com.theblissprogrammer.amazon.sdk.stores.inventories.models.InventoryType
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.network.APISessionType
import kotlinx.coroutines.Deferred

/**
 * Created by ahmedsaad on 2018-08-07.
 * Copyright (c) 2018. All rights reserved.
 **/
class InventoriesNetworkStore(val apiSession: APISessionType): InventoriesStore {

    override fun fetch(request: InventoryModels.Request): Deferred<Result<List<InventoryType>>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}