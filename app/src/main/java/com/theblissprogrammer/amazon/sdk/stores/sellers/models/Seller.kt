package com.theblissprogrammer.amazon.sdk.stores.sellers.models

import androidx.room.Entity
import androidx.room.Index
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType


/**
 * Created by ahmedsaad on 2017-11-16.
 * Copyright © 2017. All rights reserved.
 */
@Entity(primaryKeys = ["id", "marketplace"], indices = [Index(value = ["name"])])
data class Seller (
    var id: String = "",
    var marketplace: MarketplaceType = MarketplaceType.US,
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

    var marketplaceType: MarketplaceType? = null
        get() {
            return MarketplaceType.values().firstOrNull { it.id == marketplaceID }
        }
}