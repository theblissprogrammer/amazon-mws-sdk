package com.theblissprogrammer.amazon.sdk.stores.reports

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.theblissprogrammer.amazon.sdk.stores.common.CommonDAO
import com.theblissprogrammer.amazon.sdk.stores.reports.models.RequestReport

/**
 * Created by ahmed.saad on 2019-09-27.
 * Copyright © 2019. All rights reserved.
 */
@Dao
interface ReportDAO: CommonDAO<RequestReport> {
    @Query("SELECT * FROM RequestReport")
    fun fetchAll(): LiveData<List<RequestReport>>

    @Query("DELETE FROM RequestReport WHERE requestID = :id")
    fun delete(id: String)
}