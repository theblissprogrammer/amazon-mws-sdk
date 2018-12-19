package com.theblissprogrammer.amazon.sdk.stores.orders.models

import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.OrderStatus
import com.theblissprogrammer.amazon.sdk.extensions.endOfDay
import com.theblissprogrammer.amazon.sdk.extensions.startOfDay
import java.util.*

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
sealed class OrderModels {
    class Request(
            val startDate: Date = Date().startOfDay(),
            val endDate: Date = Date().endOfDay(),
            val id: String? = null,
            val orderStatuses: List<OrderStatus> = listOf(
                OrderStatus.Pending,
                OrderStatus.Unshipped,
                OrderStatus.Shipped,
                OrderStatus.Shipping,
                OrderStatus.PartiallyShipped,
                OrderStatus.PendingAvailability
            ),
            var marketplaces: List<MarketplaceType> = listOf()): OrderModels()
}