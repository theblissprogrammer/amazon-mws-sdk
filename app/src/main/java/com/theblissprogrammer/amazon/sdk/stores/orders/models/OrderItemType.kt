package com.theblissprogrammer.amazon.sdk.stores.orders.models

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
interface OrderItemType {
    var asin: String?
    var sku: String?
    var productName: String?
    var quantity: Int?
    var currency: String?
    var price: Double?
}