package com.theblissprogrammer.amazon.sdk.stores.orders.models


/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
data class OrderItem(
        override var asin: String? = null,
        override var sku: String? = null,
        override var productName: String? = null,
        override var quantity: Int? = null,
        override var currency: String? = null,
        override var price: Double? = null) : OrderItemType {

    constructor(from: OrderItemType?): this() {
        from?.let { item: OrderItemType ->
            this.asin = item.asin
            this.sku = item.sku
            this.productName = item.productName
            this.quantity = item.quantity
            this.currency = item.currency
            this.price = item.price
        }
    }
}

data class PriceComponent(
        val type: String?,
        var currency: String?,
        var amount: Double?)
