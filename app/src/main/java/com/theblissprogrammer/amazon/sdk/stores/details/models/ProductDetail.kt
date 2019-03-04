package com.theblissprogrammer.amazon.sdk.stores.details.models

import androidx.room.Embedded
import androidx.room.Relation
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product

/**
 * Created by ahmed.saad on 2019-03-04.
 * Copyright © 2019. All rights reserved.
 */
data class ProductDetail(
    @Embedded
    val detail: Detail,
    @Relation(parentColumn = "asin", entityColumn = "asin", entity = Product::class)
    val products: List<Product>
)