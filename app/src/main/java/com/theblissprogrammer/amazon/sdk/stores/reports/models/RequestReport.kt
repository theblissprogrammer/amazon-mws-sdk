package com.theblissprogrammer.amazon.sdk.stores.reports.models

import androidx.room.Entity
import com.theblissprogrammer.amazon.sdk.enums.ReportStatus
import com.theblissprogrammer.amazon.sdk.enums.ReportType
import java.util.*

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/
@Entity(primaryKeys = ["requestID"])
data class RequestReport(
        val requestID: String = "",
        val type: ReportType? = null,
        val status: ReportStatus? = null,
        val reportID: String? = null,
        val startDate: Date? = null,
        val endDate: Date? = null)