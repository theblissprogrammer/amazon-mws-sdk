package com.theblissprogrammer.amazon.sdk.stores.orders.models

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
interface OrderAddressType {
    var name: String?
    var line1: String?
    var line2: String?
    var city: String?
    var state: String?
    var postalCode: String?
    var country: String?
    var email: String?
}