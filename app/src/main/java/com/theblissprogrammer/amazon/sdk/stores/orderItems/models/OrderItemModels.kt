package com.theblissprogrammer.amazon.sdk.stores.orderItems.models

/**
 * Created by ahmed.saad on 2018-12-22.
 * Copyright © 2018. All rights reserved.
 */
sealed class OrderItemModels {
    class Request(
        val ids: List<String>
    ): OrderItemModels()
}