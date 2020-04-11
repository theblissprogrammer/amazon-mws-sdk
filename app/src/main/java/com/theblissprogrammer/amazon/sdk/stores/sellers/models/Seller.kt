package com.theblissprogrammer.amazon.sdk.stores.sellers.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType


/**
 * Created by ahmedsaad on 2017-11-16.
 * Copyright © 2017. All rights reserved.
 */
@Entity(indices = [Index(value = ["sellerId", "marketplace"], unique = true)])
data class Seller (
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var sellerId: String,
    var marketplace: MarketplaceType,
    var name: String? = null,
    var numberOfRatings: Int? = null,
    var storefrontUrl: String? = null,
    var feedbackPercent: Int? = null,
    var rating: Double? = null,
    var logo: String? = null,
    var rank: String? = null)

data class Participation (
    val marketplaceID: String,
    val sellerID: String) {

    val marketplaceType: MarketplaceType?
        get() {
            return MarketplaceType.values().firstOrNull { it.id == marketplaceID }
        }
}