package com.theblissprogrammer.amazon.sdk.stores.products

import androidx.lifecycle.LiveData
import androidx.room.*
import com.theblissprogrammer.amazon.sdk.stores.common.CommonDAO
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product


/**
 * Created by ahmed.saad on 2018-12-03.
 * Copyright © 2018. All rights reserved.
 */
@Dao
interface ProductDAO: CommonDAO<Product> {
    @Query("SELECT * FROM Product WHERE sku IN (:skus)")
    fun fetch(skus: Array<String>): LiveData<Array<Product>>

    @Query("SELECT * FROM Product WHERE sku == :sku")
    fun fetch(sku: String): LiveData<Product>
}
