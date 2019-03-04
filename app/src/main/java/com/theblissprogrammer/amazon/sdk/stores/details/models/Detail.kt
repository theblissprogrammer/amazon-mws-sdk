package com.theblissprogrammer.amazon.sdk.stores.details.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by ahmed.saad on 2019-03-04.
 * Copyright © 2019. All rights reserved.
 */
@Entity
data class Detail(
    @PrimaryKey
    var asin: String = "",
    var title: String = "",
    var category: String? = null,
    var description: String? = null,
    var features: String? = null,
    var manufacturer: String? = null,
    var manufacturerReference: String? = null,
    var weight: String? = null,
    var images: List<String> = listOf(),
    var bsr: Int? = null)