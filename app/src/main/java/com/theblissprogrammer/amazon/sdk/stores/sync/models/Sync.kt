package com.theblissprogrammer.amazon.sdk.stores.sync.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller
import java.util.*

/**
 * Created by ahmed.saad on 2019-10-05.
 * Copyright © 2019. All rights reserved.
 */
@Entity(
        foreignKeys = [
            ForeignKey(
                    entity = Seller::class,
                    parentColumns = ["id"],
                    childColumns = ["sellerKey"],
                    onDelete = ForeignKey.NO_ACTION
            )
        ],
        indices = [Index(value = ["sellerKey", "name"])],
        primaryKeys = ["name", "sellerKey"])
data class Sync(
        val name: String = "",
        val sellerKey: Long = 0,
        val updatedAt: Date? = null)