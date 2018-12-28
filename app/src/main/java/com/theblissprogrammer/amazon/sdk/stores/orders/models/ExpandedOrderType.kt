package com.theblissprogrammer.amazon.sdk.stores.orders.models

import com.theblissprogrammer.amazon.sdk.stores.fbaFees.models.FBAFeeType
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
data class ExpandedOrder(
    val order: Order,
    val fbaInventory: List<Inventory>,
    val products: List<Product>,
    val fbaFees: List<FBAFeeType>)