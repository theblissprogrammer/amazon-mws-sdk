package com.theblissprogrammer.amazon.sdk.stores.orders

import androidx.lifecycle.LiveData
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import com.theblissprogrammer.amazon.sdk.enums.OrderStatus
import com.theblissprogrammer.amazon.sdk.stores.common.CommonDAO
import com.theblissprogrammer.amazon.sdk.stores.orders.models.Order
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderAddress
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderDetail
import java.util.*


/**
 * Created by ahmed.saad on 2018-12-03.
 * Copyright © 2018. All rights reserved.
 */
@Dao
interface OrderDAO: CommonDAO<Order> {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(address: OrderAddress)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg addresses: OrderAddress)

    @Update(onConflict = OnConflictStrategy.FAIL)
    fun update(address: OrderAddress)

    @Update
    fun update(vararg addresses: OrderAddress)

    @Delete
    fun delete(vararg addresses: OrderAddress)

    @Query("SELECT * FROM `Order`")
    fun fetchAllOrders(): Array<Order>

    @Query("SELECT * FROM `Order` WHERE id = :id")
    fun fetch(id: String): LiveData<Order>

    @Transaction
    @Query("SELECT * FROM `Order` WHERE status IN (:orderStatuses) AND marketplace IN (:marketplaces) AND purchasedAt BETWEEN :startDate AND :endDate")
    fun fetch(startDate: Date, endDate: Date, orderStatuses: Array<OrderStatus>,
              marketplaces: Array<MarketplaceType>): LiveData<Array<OrderDetail>>

    @Transaction
    @Query("SELECT * FROM `Order` WHERE id = :id AND marketplace IN (:marketplaces)")
    fun fetch(id: String, marketplaces: Array<MarketplaceType>): LiveData<Array<OrderDetail>>

    @Query("SELECT * FROM `Order` WHERE id = :id AND marketplace = :marketplace")
    fun fetch(id: String, marketplace: MarketplaceType): LiveData<Order>

    @Query("SELECT * FROM `Order` WHERE marketplace = :marketplace")
    fun fetch(marketplace: MarketplaceType): LiveData<Array<Order>>

    @Query("SELECT * from `Order` ORDER BY purchasedAt ASC LIMIT 1")
    fun fetchOldestOrder(): LiveData<Order>
}

fun OrderDAO.insertOrUpdate(address: OrderAddress) {
    try {
        insert(address)
    } catch (exception: SQLiteConstraintException) {
        update(address)
    }
}
