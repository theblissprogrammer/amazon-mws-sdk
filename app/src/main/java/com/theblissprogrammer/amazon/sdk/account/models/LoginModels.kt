package com.theblissprogrammer.amazon.sdk.account.models

import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType


/**
 * Created by ahmedsaad on 2017-11-16.
 * Copyright © 2017. All rights reserved.
 */

sealed class LoginModels {
    class Request(var sellerID: String = "",
                  var token: String = "",
                  var marketplace: MarketplaceType = MarketplaceType.US) : LoginModels()
}