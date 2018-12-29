package com.theblissprogrammer.amazon.sdk.stores.inventory.models

import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.extensions.startOfDay
import java.util.*

/**
 * Created by ahmedsaad on 2018-08-07.
 * Copyright (c) 2018. All rights reserved.
 **/
sealed class InventoryModels {
    class Request(
        val skus: List<String> = listOf(), // Max 50
        var marketplace: MarketplaceType? = null,
        var lastSync: Date = Date().startOfDay() /*Change to last synced inventory datetime*/): InventoryModels()
}