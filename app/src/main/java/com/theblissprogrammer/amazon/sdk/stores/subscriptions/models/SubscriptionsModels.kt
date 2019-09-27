package com.theblissprogrammer.amazon.sdk.stores.subscriptions.models

import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType

/**
 * Created by ahmed.saad on 2019-09-27.
 * Copyright © 2019. All rights reserved.
 */
sealed class SubscriptionsModels {
    class Request(
            val name: String,
            val marketplace: MarketplaceType): SubscriptionsModels()
}