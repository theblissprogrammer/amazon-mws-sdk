package com.theblissprogrammer.amazon.sdk.stores.products.models

import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType

/**
 * Created by ahmed.saad on 2019-01-01.
 * Copyright © 2019. All rights reserved.
 */
sealed class PriceModels {
    class Request(
        val skus: List<String> = listOf(), // Max 20
        var marketplace: MarketplaceType? = null): PriceModels()
}