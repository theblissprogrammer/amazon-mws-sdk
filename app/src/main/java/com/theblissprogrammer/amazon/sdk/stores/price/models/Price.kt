package com.theblissprogrammer.amazon.sdk.stores.price.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theblissprogrammer.amazon.sdk.enums.FulfillmentChannel
import com.theblissprogrammer.amazon.sdk.enums.ItemCondition
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType

/**
 * Created by ahmed.saad on 2019-01-01.
 * Copyright © 2019. All rights reserved.
 */
@Entity
data class Price (
    @PrimaryKey
    val sku: String = "",
    val marketplace: MarketplaceType = MarketplaceType.US,
    val condition: ItemCondition? = null,
    val fulfillmentChannel: FulfillmentChannel? = null,
    val currency: String = "USD",
    val price: Double = 0.0,
    val shippingPrice: Double = 0.0,
    val regularPrice: Double = 0.0,
    @ColumnInfo(index = true)
    val sellerID: String = "",
    @ColumnInfo(index = true)
    val buyBoxWinner: Boolean = false)

data class MyPriceForSKU(
    val prices: List<Price>,
    val marketplace: MarketplaceType?)