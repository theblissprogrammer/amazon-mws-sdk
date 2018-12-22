package com.theblissprogrammer.amazon.sdk.stores.orders.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theblissprogrammer.amazon.sdk.enums.FulfillmentChannel
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.OrderStatus
import java.util.*

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
@Entity
data class Order(
    @PrimaryKey
    var id: String = "",
    @ColumnInfo(index = true)
    var purchasedAt: Date = Date(0),
    var updatedAt: Date = Date(0),
    var amount: Double? = null,
    var currency: String? = null,
    var numberOfItems: Int? = null,
    var status: OrderStatus = OrderStatus.Pending,
    var fulfillmentChannel: FulfillmentChannel? = null,
    var marketplace: MarketplaceType? = null)

data class ListOrders(
    val orders: List<ListOrder>,
    val nextToken: String?)

data class ListOrder(
    val order: Order,
    val buyer: OrderAddress?
)

data class FulfillmentData(
        var address: OrderAddress?)

data class PriceTotal(
        var currencyCode: String?,
        var amount: Double?)