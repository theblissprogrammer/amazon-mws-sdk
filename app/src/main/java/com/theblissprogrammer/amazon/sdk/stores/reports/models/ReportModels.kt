package com.theblissprogrammer.amazon.sdk.stores.reports.models

import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.ReportStatus
import com.theblissprogrammer.amazon.sdk.enums.ReportType
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/
sealed class ReportModels {

    class Request(
        val type: ReportType,
        var date: Date? = null,
        val marketplaces: List<MarketplaceType>): ReportModels()

    class ReadRequest(
        val type: ReportType,
        val id: String,
        val marketplace: MarketplaceType,
        val requestId: String): ReportModels()

    class ReportRequest(
        var requestFrom: Date? = null,
        var types: List<ReportType> = listOf(),
        var ids: ArrayList<String> = arrayListOf(),
        var statuses: List<ReportStatus> = listOf()): ReportModels()
}