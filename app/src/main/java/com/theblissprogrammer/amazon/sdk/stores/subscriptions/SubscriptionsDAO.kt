package com.theblissprogrammer.amazon.sdk.stores.subscriptions

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.stores.common.CommonDAO
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.Queue

/**
 * Created by ahmed.saad on 2019-09-27.
 * Copyright © 2019. All rights reserved.
 */
@Dao
interface SubscriptionsDAO: CommonDAO<Queue> {
    @Query("SELECT * FROM Queue WHERE name = :name AND marketplace = :marketplace")
    fun fetchQueue(name: String, marketplace: MarketplaceType): LiveData<Queue>
}