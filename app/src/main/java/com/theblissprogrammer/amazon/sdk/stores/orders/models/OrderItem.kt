package com.theblissprogrammer.amazon.sdk.stores.orders.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey


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
    ]
)
data class OrderItem(
    @PrimaryKey
    var orderItemId: String = "",
    var orderId: String = "",
    var asin: String? = null,
    @ColumnInfo(index = true)
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
