package com.theblissprogrammer.amazon.sdk.enums

/**
 * Created by ahmedsaad on 2017-11-30.
 * Copyright © 2017. All rights reserved.
 */

/// User defaults keys for strong-typed access.
/// Taken from: https://github.com/radex/SwiftyUserDefaults
open class DefaultsKeys {
    companion object {
        val sellerID = DefaultsKey<String?>("SellerID", "")
        val marketplace = DefaultsKey<String?>("marketplace", "US")
    }
}

open class DefaultsKey<out ValueType>(key: String, default: ValueType): DefaultsKeys() {
    val name: String = key
    val type: ValueType? = default
}