package com.theblissprogrammer.amazon.sdk.stores.inventories.models

/**
 * Created by ahmedsaad on 2018-08-07.
 * Copyright (c) 2018. All rights reserved.
 **/
sealed class InventoryModels {
    class Request(
            val skus: List<String>): InventoryModels()
}