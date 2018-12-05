package com.theblissprogrammer.amazon.sdk.enums

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/
enum class ReportType(val id: String) {
    OrderByOrderDate("_GET_XML_ALL_ORDERS_DATA_BY_ORDER_DATE_"),
    OrderByUpdateDate("_GET_XML_ALL_ORDERS_DATA_BY_LAST_UPDATE_"),
    InventoryAFN("_GET_AFN_INVENTORY_DATA_"),
    InventoryMFN("_GET_FLAT_FILE_OPEN_LISTINGS_DATA_"),
    AllListings("_GET_MERCHANT_LISTINGS_ALL_DATA_"),
    FBAFees("_GET_FBA_ESTIMATED_FBA_FEES_TXT_DATA_"),
    Unknown("");

    companion object {
        fun value(value: String?): ReportType = ReportType.values().firstOrNull { it.id == value } ?: Unknown
    }
}