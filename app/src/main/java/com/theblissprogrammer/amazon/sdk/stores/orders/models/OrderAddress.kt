package com.theblissprogrammer.amazon.sdk.stores.orders.models

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
data class OrderAddress(
        override var name: String? = null,
        override var line1: String? = null,
        override var line2: String? = null,
        override var city: String? = null,
        override var state: String? = null,
        override var postalCode: String? = null,
        override var country: String? = null,
        override var email: String? = null) : OrderAddressType {

    constructor(from: OrderAddressType?): this() {
        from?.let { address: OrderAddressType ->
            this.name = address.name
            this.line1 = address.line1
            this.line2 = address.line2
            this.city = address.city
            this.state = address.state
            this.postalCode = address.postalCode
            this.country = address.country
            this.email = address.email
        }
    }
}