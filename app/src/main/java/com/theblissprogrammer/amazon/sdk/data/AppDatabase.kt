package com.theblissprogrammer.amazon.sdk.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.theblissprogrammer.amazon.sdk.extensions.Converters
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellerDAO
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller

/**
 * Created by ahmed.saad on 2018-12-03.
 * Copyright © 2018. All rights reserved.
 */
@Database(entities = [Seller::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sellerDao(): SellerDAO
}