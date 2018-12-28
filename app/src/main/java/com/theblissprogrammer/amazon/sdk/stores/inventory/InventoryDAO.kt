package com.theblissprogrammer.amazon.sdk.stores.inventory

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.room.*
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory

/**
 * Created by ahmed.saad on 2018-12-21.
 * Copyright © 2018. All rights reserved.
 */
@Dao
interface InventoryDAO {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(inventory: Inventory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg inventory: Inventory)

    @Update(onConflict = OnConflictStrategy.FAIL)
    fun update(inventory: Inventory)

    @Update
    fun update(vararg inventory: Inventory)

    @Delete
    fun delete(vararg inventory: Inventory)

    @Query("SELECT * FROM Inventory")
    fun fetchAllInventorys(): Array<Inventory>

    @Query("SELECT * FROM Inventory WHERE sku = :sku")
    fun fetch(sku: String): LiveData<Inventory>

    @Query("SELECT * FROM Inventory WHERE sku IN (:skus)")
    fun fetch(skus: Array<String>): LiveData<Array<Inventory>>

    @Query("SELECT * FROM Inventory WHERE marketplace = :marketplace")
    fun fetch(marketplace: MarketplaceType): LiveData<Inventory>
}

fun InventoryDAO.insertOrUpdate(inventory: Inventory) {
    try {
        insert(inventory)
    } catch (exception: SQLiteConstraintException) {
        update(inventory)
    }
}