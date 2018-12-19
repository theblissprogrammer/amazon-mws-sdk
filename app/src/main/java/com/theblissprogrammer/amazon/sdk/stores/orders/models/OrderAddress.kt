package com.theblissprogrammer.amazon.sdk.stores.orders.models

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
data class OrderAddress(
    @PrimaryKey
    var orderId: String = "",
    var name: String? = null,
    var line1: String? = null,
    var line2: String? = null,
    var city: String? = null,
    var state: String? = null,
    var postalCode: String? = null,
    var country: String? = null,
    var email: String? = null)