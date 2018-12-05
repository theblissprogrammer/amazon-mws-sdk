package com.theblissprogrammer.amazon.sdk.stores.sellers

import androidx.lifecycle.LiveData
import androidx.room.*
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller

/**
 * Created by ahmed.saad on 2018-12-03.
 * Copyright © 2018. All rights reserved.
 */
@Dao
interface SellerDAO {
    @Update
    fun updateSellers(vararg sellers: Seller)

    @Delete
    fun deleteUsers(vararg sellers: Seller)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createSellers(vararg sellers: Seller)

    @Query("SELECT * FROM Seller")
    fun fetchAllSellers(): LiveData<Array<Seller>>

    @Query("SELECT * FROM Seller WHERE id = :id")
    fun fetch(id: String): Seller?

    @Query("SELECT * FROM Seller WHERE id = :id AND marketplace = :marketplace")
    fun fetch(id: String, marketplace: MarketplaceType): Seller?

    @Query("SELECT * FROM Seller WHERE marketplace = :marketplace")
    fun fetch(marketplace: MarketplaceType): Array<Seller>?

}