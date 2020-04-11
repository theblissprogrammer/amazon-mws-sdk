package com.theblissprogrammer.amazon.sdk.stores.sellers.models

import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType

/**
 * Created by ahmedsaad on 2018-08-03.
 * Copyright (c) 2018. All rights reserved.
 **/
sealed class SellerModels {
    class Request(
            val ids: List<String>,
            val marketplaces: List<MarketplaceType>
    ): SellerModels()

    class CurrentRequest(
            val id: String,
            val marketplace: MarketplaceType
    ): SellerModels()
}