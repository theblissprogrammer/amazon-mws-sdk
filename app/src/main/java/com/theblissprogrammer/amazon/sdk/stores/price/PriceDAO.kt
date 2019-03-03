package com.theblissprogrammer.amazon.sdk.stores.price

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.stores.common.CommonDAO
import com.theblissprogrammer.amazon.sdk.stores.price.models.Price

/**
 * Created by ahmed.saad on 2019-01-01.
 * Copyright © 2019. All rights reserved.
 */
@Dao
interface PriceDAO: CommonDAO<Price> {
    @Query("SELECT * FROM Price WHERE sku IN (:skus)")
    fun fetch(skus: Array<String>): LiveData<Array<Price>>

    @Query("SELECT * FROM Price WHERE marketplace = :marketplace")
    fun fetch(marketplace: MarketplaceType): LiveData<Array<Price>>

    @Query("SELECT * FROM Price WHERE buyBoxWinner = 1")
    fun fetchBuyBox(): LiveData<Array<Price>>

    @Query("SELECT * FROM Price WHERE sku IN (:skus) AND buyBoxWinner = 1")
    fun fetchBuyBox(skus: Array<String>): LiveData<Array<Price>>

    @Query("SELECT * FROM Price WHERE marketplace = :marketplace AND buyBoxWinner = 1")
    fun fetchBuyBox(marketplace: MarketplaceType): LiveData<Array<Price>>
}

