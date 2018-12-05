package com.theblissprogrammer.amazon.sdk.stores.orders.models

import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import java.util.*

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
sealed class OrderModels {
    class Request(
            val startDate: Date = Date(),
            val endDate: Date = Date(),
            var marketplaces: List<MarketplaceType> = listOf()): OrderModels()

    class SearchRequest(
            val id: String? = null,
            val date: Date? = null): OrderModels()

    class StartDateRequest: OrderModels()
}