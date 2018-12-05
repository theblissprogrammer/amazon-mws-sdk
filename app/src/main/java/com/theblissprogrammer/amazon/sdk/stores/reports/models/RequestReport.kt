package com.theblissprogrammer.amazon.sdk.stores.reports.models

import com.theblissprogrammer.amazon.sdk.enums.ReportStatus
import com.theblissprogrammer.amazon.sdk.enums.ReportType
import java.util.*

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/
data class RequestReport(
    val type: ReportType? = null,
    val status: ReportStatus? = null,
    val requestID: String? = null,
    val reportID: String? = null,
    val startDate: Date? = null,
    val endDate: Date? = null)