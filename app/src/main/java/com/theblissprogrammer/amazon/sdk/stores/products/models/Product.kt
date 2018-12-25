package com.theblissprogrammer.amazon.sdk.stores.products.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by ahmedsaad on 2018-08-10.
 * Copyright (c) 2018. All rights reserved.
 **/
@Entity
data class Product(
    var asin: String = "",
    @PrimaryKey
    var sku: String = "",
    var name: String = "",
    var description: String? = null,
    var imageURL: String? = null,
    var sellPrice: Double? = null,
    var buyBoxPrice: Double? = null,
    var fbaFees: Double? = null, 
    var bsr: Int? = null,
    var category: String? = null,
    var sellers: Int? = null,
    var isFBA: Boolean = false,
    var isActive: Boolean = false,
    var merchantQuantity: Int? = null)