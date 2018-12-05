package com.theblissprogrammer.amazon.sdk.enums

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
enum class OrderStatus{
    Pending,
    Unshipped,
    PartiallyShipped,
    Shipping,
    Shipped,
    PendingAvailability,
    Cancelled,
    Canceled,
    Unfulfillable
}