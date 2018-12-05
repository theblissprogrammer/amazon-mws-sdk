package com.theblissprogrammer.amazon.sdk.stores.inventories

import com.theblissprogrammer.amazon.sdk.extensions.coroutineCompletionOnUi
import com.theblissprogrammer.amazon.sdk.stores.inventories.models.InventoryModels
import com.theblissprogrammer.amazon.sdk.stores.inventories.models.InventoryType
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse

/**
 * Created by ahmedsaad on 2018-08-07.
 * Copyright (c) 2018. All rights reserved.
 **/
class InventoriesWorker(val store: InventoriesStore,
                        val cacheStore: InventoriesCacheStore?
): InventoriesWorkerType {

    override fun fetch(request: InventoryModels.Request, completion: CompletionResponse<List<InventoryType>>) {
        if (cacheStore != null)
            coroutineCompletionOnUi(completion) {
                completion(cacheStore.fetch(request).await())
            }
    }
}