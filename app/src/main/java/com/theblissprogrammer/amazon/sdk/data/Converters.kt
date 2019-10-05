package com.theblissprogrammer.amazon.sdk.data

import androidx.room.TypeConverter
import com.theblissprogrammer.amazon.sdk.enums.*
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Quantity
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.QuantityDetail
import java.lang.IllegalArgumentException
import java.util.*

/**
 * Created by ahmed.saad on 2018-12-04.
 * Copyright © 2018. All rights reserved.
 */
class Converters {
    private val delimiter = "[SALESTRACKERDELIMITER]"

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun marketplaceFromString(value: String?): MarketplaceType? {
        return try {
            MarketplaceType.valueOf(value ?: "")
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    @TypeConverter
    fun marketplaceToString(marketplace: MarketplaceType?): String? {
        return marketplace?.name
    }

    @TypeConverter
    fun orderStatusfromString(value: String?): OrderStatus? {
        return try {
            OrderStatus.valueOf(value ?: "")
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    @TypeConverter
    fun orderStatusToString(orderStatus: OrderStatus?): String? {
        return orderStatus?.name
    }

    @TypeConverter
    fun fulfillmentChannelfromString(value: String?): FulfillmentChannel? {
        return try {
            FulfillmentChannel.valueOf(value ?: "")
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    @TypeConverter
    fun fulfillmentChannelToString(fulfillmentChannel: FulfillmentChannel?): String? {
        return fulfillmentChannel?.name
    }

    @TypeConverter
    fun inventoryConditionfromString(value: String?): InventoryCondition? {
        return try {
            InventoryCondition.valueOf(value ?: "")
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    @TypeConverter
    fun inventoryConditionToString(inventoryCondition: InventoryCondition?): String? {
        return inventoryCondition?.name
    }

    @TypeConverter
    fun quantityfromString(value: String?): Quantity? {
        return if (!value.isNullOrEmpty()) Quantity(value) else null
    }

    @TypeConverter
    fun quantityToString(quantity: Quantity?): String? {
        return quantity?.toString()
    }

    @TypeConverter
    fun quantityDetailfromString(value: String?): QuantityDetail? {
        return if (!value.isNullOrEmpty()) QuantityDetail(value) else null
    }

    @TypeConverter
    fun quantityDetailToString(quantity: QuantityDetail?): String? {
        return quantity?.toString()
    }

    @TypeConverter
    fun itemConditionfromString(value: String?): ItemCondition? {
        return try {
            ItemCondition.valueOf(value ?: "")
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    @TypeConverter
    fun itemConditionToString(itemCondition: ItemCondition?): String? {
        return itemCondition?.name
    }

    @TypeConverter
    fun listfromString(value: String?): List<String>? {
        return try {
            return value?.split(delimiter)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    @TypeConverter
    fun listToString(list: List<String>?): String? {
        return list?.joinToString(separator = delimiter)
    }
}