package com.theblissprogrammer.amazon.sdk.stores.inventories.models

import com.theblissprogrammer.amazon.sdk.enums.InventoryCondition

/**
 * Created by ahmedsaad on 2018-08-07.
 * Copyright (c) 2018. All rights reserved.
 **/
interface InventoryType {
    var sku: String
    var asin: String
    var condition: InventoryCondition
    var quantity: Int
}