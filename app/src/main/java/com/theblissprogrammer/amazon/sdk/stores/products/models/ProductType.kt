package com.theblissprogrammer.amazon.sdk.stores.products.models

/**
 * Created by ahmedsaad on 2018-08-10.
 * Copyright (c) 2018. All rights reserved.
 **/
interface ProductType {
    var asin: String
    var sku: String
    var name: String
    var description: String?
    var imageURL: String?
    var sellPrice: Double?
    var buyBoxPrice: Double?
    var fbaFees: Double?
    var bsr: Int?
    var category: String?
    var sellers: Int?
    var isFBA: Boolean
    var isActive: Boolean
    var merchantQuantity: Int?
}