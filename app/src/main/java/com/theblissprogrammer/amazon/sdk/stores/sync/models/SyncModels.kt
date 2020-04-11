package com.theblissprogrammer.amazon.sdk.stores.sync.models

import java.util.*

/**
 * Created by ahmed.saad on 2019-10-07.
 * Copyright © 2019. All rights reserved.
 */
sealed class SyncModels {
    class Request(
            val name: String,
            val sellerKey: Long): SyncModels()

    class UpdateRequest(
            val name: String,
            val sellerKey: Long,
            val updatedAt: Date): SyncModels()
}