package com.theblissprogrammer.amazon.sdk.stores.orders.models

import com.theblissprogrammer.amazon.sdk.enums.OrderStatus
import java.util.*

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
data class Order(
    override var id: String = "",
    override var purchasedAt: Date = Date(0),
    override var updatedAt: Date = Date(0),
    override var status: OrderStatus = OrderStatus.Pending,
    override var salesChannel: String? = null,
    override var buyer: OrderAddress? = null,
    override var items: ArrayList<OrderItem>? = null): OrderType {


    constructor(from: OrderType?): this() {
        from?.let { order: OrderType ->
            this.id = order.id
            this.purchasedAt = order.purchasedAt
            this.updatedAt = order.updatedAt
            this.status = order.status
            this.salesChannel = order.salesChannel
            this.buyer = order.buyer
            this.items = order.items
        }
    }
}

data class FulfillmentData(
        var address: OrderAddress?)