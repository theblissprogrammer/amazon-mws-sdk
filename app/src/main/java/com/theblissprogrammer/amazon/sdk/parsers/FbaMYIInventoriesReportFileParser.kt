package com.theblissprogrammer.amazon.sdk.parsers

import com.theblissprogrammer.amazon.sdk.enums.InventoryCondition
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Quantity


/**
 * Created by ahmedsaad on 2018-02-20.
 * Copyright © 2017. All rights reserved.
 */
class FbaMYIInventoriesReportFileParser {

    fun parse(input: String): List<Inventory> {
        val products = input.split(Regex("[\r\n]+"))
                .asSequence()
                .mapNotNull { it.split("\t") }

        val header = products.firstOrNull { it.contains("sku") } ?: return listOf()

        val skuIndex = header.indexOf("sku")
        val fnskuIndex = header.indexOf("fnsku")
        val asinIndex = header.indexOf("asin")
        val nameIndex = header.indexOf("product-name")
        val conditionIndex = header.indexOf("condition")
        val priceIndex = header.indexOf("your-price")
        val mfnExisitsIndex = header.indexOf("mfn-listing-exists")
        val mfnQuantityIndex = header.indexOf("mfn-fulfillable-quantity")
        val afnExisitsIndex = header.indexOf("afn-listing-exists")
        val afnWarehouseIndex = header.indexOf("afn-warehouse-quantity")
        val afnFulfillableIndex = header.indexOf("afn-fulfillable-quantity")
        val afnUnsellableIndex = header.indexOf("afn-unsellable-quantity")
        val afnReservedIndex = header.indexOf("afn-reserved-quantity")
        val afnTotalIndex = header.indexOf("afn-total-quantity")
        val volumeIndex = header.indexOf("per-unit-volume")
        val inboundWorkingIndex = header.indexOf("afn-inbound-working-quantity")
        val inboundShippedIndex = header.indexOf("afn-inbound-shipped-quantity")
        val inboundReceivingIndex = header.indexOf("afn-inbound-receiving-quantity")
        val reservedFutureIndex = header.indexOf("afn-reserved-future-supply")
        val futureSupplyIndex = header.indexOf("afn-future-supply-buyable")

        return products.mapNotNull {
            if (it.isNullOrEmpty() || it.contains("sku")
                    || it[conditionIndex] == InventoryCondition.Unknown.name) return@mapNotNull  null

            Inventory(
                    sku = it[skuIndex],
                    asin = it[asinIndex],
                    fnsku = it[fnskuIndex],
                    condition = InventoryCondition.valueOf(it[conditionIndex]),
                    quantity = Quantity(total = it[afnTotalIndex].toIntOrNull() ?: 0)
            )
        }.toList()
    }
}