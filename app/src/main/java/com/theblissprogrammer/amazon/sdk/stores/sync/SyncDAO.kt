package com.theblissprogrammer.amazon.sdk.stores.sync

import androidx.room.Dao
import androidx.room.Query
import com.theblissprogrammer.amazon.sdk.stores.common.CommonDAO
import com.theblissprogrammer.amazon.sdk.stores.sync.models.Sync
import java.util.*

/**
 * Created by ahmed.saad on 2019-10-05.
 * Copyright © 2019. All rights reserved.
 */
@Dao
interface SyncDAO: CommonDAO<Sync> {
    @Query("SELECT updatedAt FROM Sync WHERE name = :name AND sellerKey = :sellerKey")
    fun fetchUpdatedAt(name: String, sellerKey: Long): Date
}