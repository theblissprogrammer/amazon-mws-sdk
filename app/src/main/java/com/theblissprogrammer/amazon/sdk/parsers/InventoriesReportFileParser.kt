package com.theblissprogrammer.amazon.sdk.parsers

import com.theblissprogrammer.amazon.sdk.enums.InventoryCondition
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Quantity


/**
 * Created by ahmedsaad on 2018-02-20.
 * Copyright © 2017. All rights reserved.
 */

data class InventoriesReportFileModel (
        val inventories: List<Inventory>
)

class InventoriesReportFileParser(val marketplace: MarketplaceType) {

    fun parse(input: String): InventoriesReportFileModel? {
        val products = input.split(Regex("[\r\n]+"))
                .asSequence()
                .mapNotNull { it.split("\t") }

        val header = products.firstOrNull { it.contains("seller-sku")} ?: return null

        val skuIndex = header.indexOf("seller-sku")
        val asinIndex = header.indexOf("asin")
        val conditionIndex = header.indexOf("condition-type")
        val warehouseConditionIndex = header.indexOf("Warehouse-Condition-code")
        val quantityIndex = header.indexOf("Quantity Available")

        val inventory = products.mapNotNull {
            if (it.contains("seller-sku") || it[warehouseConditionIndex] == "UNSELLABLE") null
            else
                Inventory(
                        sku = it[skuIndex],
                        asin = it[asinIndex],
                        condition = InventoryCondition.valueOf(it[conditionIndex]),
                        marketplace = marketplace,
                        quantity = Quantity(total = it[quantityIndex].toInt())
                )
        }.toList()

        return InventoriesReportFileModel(
                inventories = inventory
        )
    }
}