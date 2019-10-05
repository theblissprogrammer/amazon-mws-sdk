package com.theblissprogrammer.amazon.sdk.stores.orderItems.models

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.orders.models.Order
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product


/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(value = ["orderId"]),
        Index(value = ["sku"])
    ], primaryKeys = ["orderItemId","orderId"]
)
data class OrderItem(
    var orderItemId: String = "",
    var orderId: String = "",
    var asin: String? = null,
    var sku: String? = null,
    var productName: String? = null,
    var quantity: Int? = null,
    var currency: String? = null,
    var price: Double? = null)

data class PriceComponent(
        val type: String?,
        var currency: String?,
        var amount: Double?)

data class ListOrderItems(
    val orderItems: List<OrderItem>,
    val orderId: String?,
    val nextToken: String?)

data class OrderItemDetail(
        @Embedded
        val item: OrderItem,
        @Relation(parentColumn = "sku", entityColumn = "sku", entity = Inventory::class)
        val inventories: List<Inventory> = listOf(),
        @Relation(parentColumn = "sku", entityColumn = "sku", entity = Product::class)
        val products: List<Product> = listOf()
)
