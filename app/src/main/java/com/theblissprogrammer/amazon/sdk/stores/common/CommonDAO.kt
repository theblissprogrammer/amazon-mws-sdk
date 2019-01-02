package com.theblissprogrammer.amazon.sdk.stores.common

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * Created by ahmed.saad on 2019-01-01.
 * Copyright © 2019. All rights reserved.
 */
interface CommonDAO<T> {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(entity: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg entity: T)

    @Update(onConflict = OnConflictStrategy.FAIL)
    fun update(entity: T)

    @Update
    fun update(vararg entity: T)

    @Delete
    fun delete(vararg entity: T)
}

fun <T>CommonDAO<T>.insertOrUpdate(entity: T) {
    try {
        insert(entity)
    } catch (exception: SQLiteConstraintException) {
        update(entity)
    }
}