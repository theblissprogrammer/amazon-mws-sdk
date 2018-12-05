package com.theblissprogrammer.amazon.sdk.stores.orders.models

import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.OrderStatus
import com.theblissprogrammer.amazon.sdk.stores.fbaFees.models.FBAFeeType
import com.theblissprogrammer.amazon.sdk.stores.inventories.models.InventoryType
import com.theblissprogrammer.amazon.sdk.stores.products.models.ProductType
import java.util.*

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
interface OrderType {
    var id: String
    var purchasedAt: Date
    var updatedAt: Date
    var status: OrderStatus
    var salesChannel: String?
    var buyer: OrderAddress?
    var items: ArrayList<OrderItem>?

    val marketplace: MarketplaceType
        get() = MarketplaceType.values().firstOrNull { it.salesChannel == salesChannel } ?: MarketplaceType.US

    val intID: Int
        get() = id.replace(Regex("[^1-9]+"), "").takeLast(9).toInt()
}

data class ExpandedOrderType(
    val order: OrderType,
    val fbaInventory: List<InventoryType>,
    val products: List<ProductType>,
    val fbaFees: List<FBAFeeType>)