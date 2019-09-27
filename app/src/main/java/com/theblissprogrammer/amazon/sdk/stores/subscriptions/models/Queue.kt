package com.theblissprogrammer.amazon.sdk.stores.subscriptions.models

import androidx.room.Entity
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType

/**
 * Created by ahmed.saad on 2019-09-26.
 * Copyright © 2019. All rights reserved.
 */
@Entity(primaryKeys = ["name"])
data class Queue(
        val name: String = "",
        val url: String? = null,
        val marketplace: MarketplaceType = MarketplaceType.US)