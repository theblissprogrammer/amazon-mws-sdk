package com.theblissprogrammer.amazon.sdk.stores.details

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.theblissprogrammer.amazon.sdk.stores.common.CommonDAO
import com.theblissprogrammer.amazon.sdk.stores.details.models.Detail
import com.theblissprogrammer.amazon.sdk.stores.details.models.ProductDetail

/**
 * Created by ahmed.saad on 2019-03-04.
 * Copyright © 2019. All rights reserved.
 */
@Dao
interface DetailDAO: CommonDAO<Detail> {
    @Query("SELECT * FROM Detail WHERE asin IN (:asins)")
    fun fetch(asins: Array<String>): LiveData<Array<Detail>>

    @Query("SELECT * FROM Detail WHERE asin == :asin")
    fun fetch(asin: String): LiveData<Detail>

    @Transaction
    @Query("SELECT * from Detail WHERE Detail.asin IN (:asins)")
    fun fetchProductDetails(asins: Array<String>): LiveData<List<ProductDetail>>
}