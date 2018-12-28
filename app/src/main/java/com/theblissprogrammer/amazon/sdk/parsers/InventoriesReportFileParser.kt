package com.theblissprogrammer.amazon.sdk.parsers

import com.theblissprogrammer.amazon.sdk.enums.InventoryCondition
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Quantity


/**
 * Created by ahmedsaad on 2018-02-20.
 * Copyright © 2017. All rights reserved.
 */
class InventoriesReportFileParser {

    fun parse(input: String): List<Inventory> {
        val products = input.split(Regex("[\r\n]+")).filter { it.isNotEmpty() }
                .map { it.split("\t") }
        val header = products.firstOrNull { it.contains("seller-sku")} ?: return listOf()

        val skuIndex = header.indexOf("seller-sku")
        val asinIndex = header.indexOf("asin")
        val coniditionIndex = header.indexOf("Warehouse-Condition-code")
        val quantityIndex = header.indexOf("Quantity Available")

        return products.filter { !it.contains("seller-sku") }.map {
            Inventory(
                    sku = it[skuIndex],
                    asin = it[asinIndex],
                    condition = InventoryCondition.valueOf(it[coniditionIndex]),
                    quantity = Quantity(total = it[quantityIndex].toInt())
            )
        }
    }
}