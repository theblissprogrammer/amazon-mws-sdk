package com.theblissprogrammer.amazon.sdk.stores.inventories.models

import com.theblissprogrammer.amazon.sdk.enums.InventoryCondition

/**
 * Created by ahmedsaad on 2018-08-07.
 * Copyright (c) 2018. All rights reserved.
 **/
data class Inventory(
    override var sku: String = "",
    override var asin: String = "",
    override var condition: InventoryCondition = InventoryCondition.UNSELLABLE,
    override var quantity: Int = 0) : InventoryType {

    constructor(from: InventoryType?): this() {
        from?.let {inventory ->
            this.sku = inventory.sku
            this.asin = inventory.asin
            this.condition = inventory.condition
            this.quantity = inventory.quantity
        }
    }
}