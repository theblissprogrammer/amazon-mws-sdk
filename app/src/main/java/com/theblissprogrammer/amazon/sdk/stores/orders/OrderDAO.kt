package com.theblissprogrammer.amazon.sdk.stores.orders

import androidx.lifecycle.LiveData
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import com.theblissprogrammer.amazon.sdk.enums.OrderStatus
import com.theblissprogrammer.amazon.sdk.stores.orders.models.Order
import java.util.*


/**
 * Created by ahmed.saad on 2018-12-03.
 * Copyright © 2018. All rights reserved.
 */
@Dao
interface OrderDAO {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(order: Order)

    @Update(onConflict = OnConflictStrategy.FAIL)
    fun update(order: Order)

    @Update
    fun update(vararg orders: Order)

    @Delete
    fun delete(vararg orders: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg orders: Order)

    @Query("SELECT * FROM `Order`")
    fun fetchAllOrders(): LiveData<Array<Order>>

    @Query("SELECT * FROM `Order` WHERE id = :id")
    fun fetch(id: String): LiveData<Order>

    @Query("SELECT * FROM `Order` WHERE status IN (:orderStatuses) AND marketplace IN (:marketplaces) AND purchasedAt BETWEEN :startDate AND :endDate")
    fun fetch(startDate: Date, endDate: Date, orderStatuses: List<OrderStatus>, marketplaces: List<MarketplaceType>): LiveData<List<Order>>

    @Query("SELECT * FROM `Order` WHERE id = :id AND marketplace IN (:marketplaces)")
    fun fetch(id: String, marketplaces: List<MarketplaceType>): LiveData<List<Order>>

    @Query("SELECT * FROM `Order` WHERE id = :id AND marketplace = :marketplace")
    fun fetch(id: String, marketplace: MarketplaceType): LiveData<Order>

    @Query("SELECT * FROM `Order` WHERE marketplace = :marketplace")
    fun fetch(marketplace: MarketplaceType): LiveData<Array<Order>>

    @Query("SELECT * from `Order` ORDER BY purchasedAt ASC LIMIT 1")
    fun fetchOldestOrder(): LiveData<Order>
}


fun OrderDAO.insertOrUpdate(order: Order) {
    try {
        insert(order)
    } catch (exception: SQLiteConstraintException) {
        update(order)
    }
}
