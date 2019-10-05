package com.theblissprogrammer.amazon.sdk.stores.inventory.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.theblissprogrammer.amazon.sdk.enums.InventoryCondition
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.SupplyType
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product

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
    var condition: InventoryCondition = InventoryCondition.New,
    var quantityDetail: QuantityDetail? = null,
    var quantity: Quantity = Quantity())

data class QuantityDetail(
        var mfnQuantity: Int = 0,
        var afnWarehouseQuantity: Int = 0,
        var afnFulfillableQuantity: Int = 0,
        var afnUnsellableQuantity: Int = 0,
        var afnReservedQuantity: Int = 0,
        var afnTotalQuantity: Int = 0,
        var inboundWorkingQuantity: Int = 0,
        var inboundShippedQuantity: Int = 0,
        var inboundReceivedQuantity: Int = 0) {

    constructor(string: String): this() {
        val values = string.split(delimiter)

        this.mfnQuantity = values[0].toIntOrNull() ?: 0
        this.afnWarehouseQuantity = values[1].toIntOrNull() ?: 0
        this.afnFulfillableQuantity = values[2].toIntOrNull() ?: 0
        this.afnUnsellableQuantity = values[3].toIntOrNull() ?: 0
        this.afnReservedQuantity = values[4].toIntOrNull() ?: 0
        this.afnTotalQuantity = values[5].toIntOrNull() ?: 0
        this.inboundWorkingQuantity = values[6].toIntOrNull() ?: 0
        this.inboundShippedQuantity = values[7].toIntOrNull() ?: 0
        this.inboundReceivedQuantity = values[8].toIntOrNull() ?: 0
    }

    override fun toString(): String {
        return listOf(mfnQuantity, afnWarehouseQuantity, afnFulfillableQuantity,
                afnUnsellableQuantity, afnReservedQuantity, afnTotalQuantity,
                inboundWorkingQuantity, inboundShippedQuantity, inboundReceivedQuantity).joinToString(separator = delimiter)
    }

    companion object {
        const val delimiter = " | "
    }
}

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

data class InventoryDetail(
        @Embedded
        val inventory: Inventory,
        @Relation(parentColumn = "sku", entityColumn = "sku", entity = Product::class)
        val products: List<Product>
)