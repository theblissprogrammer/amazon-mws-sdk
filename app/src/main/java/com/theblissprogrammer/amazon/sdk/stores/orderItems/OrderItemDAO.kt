package com.theblissprogrammer.amazon.sdk.stores.orderItems

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.room.*
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.OrderItem

/**
 * Created by ahmed.saad on 2018-12-21.
 * Copyright © 2018. All rights reserved.
 */
@Dao
interface OrderItemDAO {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(orderItem: OrderItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg orderItems: OrderItem)

    @Update(onConflict = OnConflictStrategy.FAIL)
    fun update(orderItem: OrderItem)

    @Update
    fun update(vararg orderItems: OrderItem)

    @Delete
    fun delete(vararg orderItems: OrderItem)

    @Query("SELECT * FROM OrderItem")
    fun fetchAllOrderItems(): Array<OrderItem>

    @Query("SELECT * FROM OrderItem WHERE orderItemId = :id")
    fun fetch(id: String): LiveData<OrderItem>

    @Query("SELECT * FROM OrderItem WHERE orderId = :id")
    fun fetchByOrderId(id: String): LiveData<List<OrderItem>>

    @Query("SELECT * FROM OrderItem WHERE orderId IN (:ids)")
    fun fetchByOrderId(ids: Array<String>): LiveData<Array<OrderItem>>
}

fun OrderItemDAO.insertOrUpdate(orderItem: OrderItem) {
    try {
        insert(orderItem)
    } catch (exception: SQLiteConstraintException) {
        update(orderItem)
    }
}