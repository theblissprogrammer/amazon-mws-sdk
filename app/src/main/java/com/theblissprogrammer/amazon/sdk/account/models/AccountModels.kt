package com.theblissprogrammer.amazon.sdk.account.models

import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller


/**
 * Created by ahmedsaad on 2018-02-07.
 * Copyright © 2017. All rights reserved.
 */
sealed class AccountModels {
    class ServerResponse(
        val sellers: List<Seller>,
        val token: String): AccountModels()

    class Response(
            val seller: Seller
    ): AccountModels()
}