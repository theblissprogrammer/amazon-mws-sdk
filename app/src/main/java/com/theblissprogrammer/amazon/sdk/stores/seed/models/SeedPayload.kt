package com.theblissprogrammer.amazon.sdk.stores.seed.models

import com.theblissprogrammer.amazon.sdk.stores.fbaFees.models.FBAFeeType
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.orders.models.Order
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product

/**
 * Created by ahmedsaad on 2018-08-23.
 * Copyright (c) 2018. All rights reserved.
 **/
data class SeedPayload(
    var orders: List<Order> = listOf(),
    var inventories: List<Inventory> = listOf(),
    var products: List<Product> = listOf(),
    var fbaFees: List<FBAFeeType> = listOf()) {

    val isEmpty: Boolean
        get() = orders.isEmpty() && inventories.isEmpty() && products.isEmpty()
}