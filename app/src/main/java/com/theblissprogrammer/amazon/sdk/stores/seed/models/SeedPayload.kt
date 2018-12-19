package com.theblissprogrammer.amazon.sdk.stores.seed.models

import com.theblissprogrammer.amazon.sdk.stores.fbaFees.models.FBAFeeType
import com.theblissprogrammer.amazon.sdk.stores.inventories.models.InventoryType
import com.theblissprogrammer.amazon.sdk.stores.orders.models.Order
import com.theblissprogrammer.amazon.sdk.stores.products.models.ProductType

/**
 * Created by ahmedsaad on 2018-08-23.
 * Copyright (c) 2018. All rights reserved.
 **/
data class SeedPayload(
        var orders: List<Order> = listOf(),
        var inventories: List<InventoryType> = listOf(),
        var products: List<ProductType> = listOf(),
        var fbaFees: List<FBAFeeType> = listOf()) {

    val isEmpty: Boolean
        get() = orders.isEmpty() && inventories.isEmpty() && products.isEmpty()
}