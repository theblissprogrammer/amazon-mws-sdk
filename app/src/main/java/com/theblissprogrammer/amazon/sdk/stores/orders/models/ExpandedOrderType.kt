package com.theblissprogrammer.amazon.sdk.stores.orders.models

import com.theblissprogrammer.amazon.sdk.stores.fbaFees.models.FBAFeeType
import com.theblissprogrammer.amazon.sdk.stores.inventories.models.InventoryType
import com.theblissprogrammer.amazon.sdk.stores.products.models.ProductType

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
data class ExpandedOrder(
    val order: Order,
    val fbaInventory: List<InventoryType>,
    val products: List<ProductType>,
    val fbaFees: List<FBAFeeType>)