package com.theblissprogrammer.amazon.sdk.stores.orderItems

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.room.*
import com.theblissprogrammer.amazon.sdk.stores.common.CommonDAO
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.OrderItem

/**
 * Created by ahmed.saad on 2018-12-21.
 * Copyright © 2018. All rights reserved.
 */
@Dao
interface OrderItemDAO: CommonDAO<OrderItem> {
    @Query("SELECT * FROM OrderItem")
    fun fetchAllOrderItems(): Array<OrderItem>

    @Query("SELECT * FROM OrderItem WHERE orderItemId = :id")
    fun fetch(id: String): LiveData<OrderItem>

    @Query("SELECT * FROM OrderItem WHERE orderId = :id")
    fun fetchByOrderId(id: String): LiveData<Array<OrderItem>>

    @Query("SELECT * FROM OrderItem WHERE orderId IN (:ids)")
    fun fetchByOrderId(ids: Array<String>): LiveData<Array<OrderItem>>
}