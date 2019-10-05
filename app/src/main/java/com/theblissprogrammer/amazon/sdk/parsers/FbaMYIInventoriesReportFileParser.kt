package com.theblissprogrammer.amazon.sdk.parsers

import com.theblissprogrammer.amazon.sdk.enums.InventoryCondition
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.InventoryDetail
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Quantity
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.QuantityDetail
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product


/**
 * Created by ahmedsaad on 2018-02-20.
 * Copyright © 2017. All rights reserved.
 */
data class FbaMYIInventoriesReportModel (
        val inventories: Sequence<Inventory>,
        val products: Sequence<Product>
)

class FbaMYIInventoriesReportFileParser(val marketplace: MarketplaceType) {

    fun parse(input: String): FbaMYIInventoriesReportModel? {
        val products = input.split(Regex("[\r\n]+"))
                .asSequence()
                .mapNotNull { it.split("\t") }

        val header = products.firstOrNull { it.contains("sku") } ?: return  null

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

        val inventory = products.mapNotNull {
            if (it.isNullOrEmpty() || it.contains("sku")
                    || it[conditionIndex] == InventoryCondition.Unknown.name) return@mapNotNull  null

            val inventory = Inventory(
                    sku = it[skuIndex],
                    asin = it[asinIndex],
                    fnsku = it[fnskuIndex],
                    condition = InventoryCondition.valueOf(it[conditionIndex]),
                    marketplace = marketplace,
                    quantityDetail = QuantityDetail(
                            mfnQuantity = it[mfnQuantityIndex].toIntOrNull() ?: 0,
                            afnWarehouseQuantity = it[afnWarehouseIndex].toIntOrNull() ?: 0,
                            afnFulfillableQuantity = it[afnFulfillableIndex].toIntOrNull() ?: 0,
                            afnUnsellableQuantity = it[afnUnsellableIndex].toIntOrNull() ?: 0,
                            afnReservedQuantity = it[afnReservedIndex].toIntOrNull() ?: 0,
                            afnTotalQuantity = it[afnTotalIndex].toIntOrNull() ?: 0,
                            inboundWorkingQuantity = it[inboundWorkingIndex].toIntOrNull() ?: 0,
                            inboundShippedQuantity = it[inboundShippedIndex].toIntOrNull() ?: 0,
                            inboundReceivedQuantity = it[inboundReceivingIndex].toIntOrNull() ?: 0
                    ),
                    quantity = Quantity(total = it[afnTotalIndex].toIntOrNull() ?: 0)
            )

            val product = Product(
                    sku = it[skuIndex],
                    asin = it[asinIndex],
                    name = it[nameIndex],
                    sellPrice = it[priceIndex].toDoubleOrNull(),
                    isFBA = it[afnExisitsIndex].equals("yes", ignoreCase = true)
            )

            Pair(
                    inventory,
                    product
            )
        }

        return FbaMYIInventoriesReportModel(
                inventories = inventory.map { it.first },
                products = inventory.map { it.second }
        )
    }
}