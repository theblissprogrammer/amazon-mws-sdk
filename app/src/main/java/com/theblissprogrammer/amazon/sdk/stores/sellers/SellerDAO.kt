package com.theblissprogrammer.amazon.sdk.stores.sellers

import androidx.lifecycle.LiveData
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller
import android.database.sqlite.SQLiteConstraintException
import androidx.room.*


/**
 * Created by ahmed.saad on 2018-12-03.
 * Copyright © 2018. All rights reserved.
 */
@Dao
interface SellerDAO {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(seller: Seller)

    @Update(onConflict = OnConflictStrategy.FAIL)
    fun update(seller: Seller)

    @Update
    fun update(vararg sellers: Seller)

    @Delete
    fun delete(vararg sellers: Seller)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg sellers: Seller)

    @Query("SELECT * FROM Seller")
    fun fetchAllSellers(): LiveData<Array<Seller>>

    @Query("SELECT * FROM Seller WHERE id = :id")
    fun fetch(id: String): LiveData<Seller>

    @Query("SELECT * FROM Seller WHERE id = :id AND marketplace = :marketplace")
    fun fetch(id: String, marketplace: MarketplaceType): LiveData<Seller>

    @Query("SELECT * FROM Seller WHERE marketplace = :marketplace")
    fun fetch(marketplace: MarketplaceType): LiveData<Array<Seller>>
}


fun SellerDAO.insertOrUpdate(seller: Seller) {
    try {
        insert(seller)
    } catch (exception: SQLiteConstraintException) {
        update(seller)
    }
}
