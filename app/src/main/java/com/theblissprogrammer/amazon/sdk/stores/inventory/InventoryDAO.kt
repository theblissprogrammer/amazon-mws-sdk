package com.theblissprogrammer.amazon.sdk.stores.inventory

import androidx.lifecycle.LiveData
import androidx.room.*
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.stores.common.CommonDAO
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.InventoryDetail

/**
 * Created by ahmed.saad on 2018-12-21.
 * Copyright © 2018. All rights reserved.
 */
@Dao
interface InventoryDAO: CommonDAO<Inventory> {
    @Query("SELECT * FROM Inventory")
    fun fetchAllInventories(): Array<Inventory>

    @Query("SELECT * FROM Inventory WHERE sku = :sku")
    fun fetch(sku: String): LiveData<Inventory>

    @Query("SELECT * FROM Inventory WHERE sku = :sku")
    fun fetchSync(sku: String): Inventory

    @Transaction
    @Query("SELECT * FROM Inventory WHERE sku IN (:skus)")
    fun fetch(skus: Array<String>): LiveData<Array<InventoryDetail>>

    @Transaction
    @Query("SELECT * FROM Inventory WHERE marketplace = :marketplace")
    fun fetch(marketplace: MarketplaceType): LiveData<Array<InventoryDetail>>
}