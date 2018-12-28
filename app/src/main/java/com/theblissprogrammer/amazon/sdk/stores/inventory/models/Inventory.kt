package com.theblissprogrammer.amazon.sdk.stores.inventory.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theblissprogrammer.amazon.sdk.enums.InventoryCondition
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.SupplyType

/**
 * Created by ahmedsaad on 2018-08-07.
 * Copyright (c) 2018. All rights reserved.
 **/
@Entity
data class Inventory(
    @PrimaryKey
    var sku: String = "",
    var asin: String = "",
    var fnsku: String = "",
    var marketplace: MarketplaceType? = null,
    var condition: InventoryCondition = InventoryCondition.NewItem,
    var quantity: Quantity = Quantity())

data class Quantity(
    var instock: Int = 0,
    var transfer: Int = 0,
    var inbound: Int = 0,
    var total: Int = 0) {

    constructor(string: String): this() {
        val values = string.split(delimiter)

        this.instock = values[0].toIntOrNull() ?: 0
        this.transfer = values[1].toIntOrNull() ?: 0
        this.inbound = values[2].toIntOrNull() ?: 0
        this.total = values[3].toIntOrNull() ?: 0
    }

    override fun toString(): String {
        return listOf(instock, transfer, inbound, total).joinToString(separator = delimiter)
    }

    companion object {
        const val delimiter = " | "
    }
}

data class QuantityType(
    var supplyType: SupplyType,
    var quantity: Int)

data class ListInventorySupply(
    val inventory: List<Inventory>,
    val marketplace: MarketplaceType?,
    val nextToken: String?)