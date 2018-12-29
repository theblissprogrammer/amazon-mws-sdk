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
        var lastSync: Date = Date().startOfDay() /* TODO: Change to last synced order datetime */,
        val startDate: Date? = null, /* To filter by a specific time frame set startDate and endDate */
        val endDate: Date = Date().endOfDay(),
        val id: String? = null,
        val orderStatuses: List<OrderStatus> = listOf(
            OrderStatus.Pending,
            OrderStatus.Unshipped,
            OrderStatus.Shipped,
            OrderStatus.PartiallyShipped,
            OrderStatus.PendingAvailability
        ),
        var marketplaces: List<MarketplaceType> = listOf()): OrderModels()
}