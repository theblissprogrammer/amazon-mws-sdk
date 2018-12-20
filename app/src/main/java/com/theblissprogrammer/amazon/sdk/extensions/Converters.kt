package com.theblissprogrammer.amazon.sdk.extensions

import androidx.room.TypeConverter
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.OrderStatus
import java.util.*

/**
 * Created by ahmed.saad on 2018-12-04.
 * Copyright © 2018. All rights reserved.
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun marketplaceFromString(value: String?): MarketplaceType? {
        return MarketplaceType.valueOf(value ?: "")
    }

    @TypeConverter
    fun marketplaceToString(marketplace: MarketplaceType?): String? {
        return marketplace?.name
    }

    @TypeConverter
    fun orderStatusfromString(value: String?): OrderStatus? {
        return OrderStatus.valueOf(value ?: "")
    }

    @TypeConverter
    fun orderStatusToString(orderStatus: OrderStatus?): String? {
        return orderStatus?.name
    }
}