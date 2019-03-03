package com.theblissprogrammer.amazon.sdk.stores.reports

import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetwork
import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
import com.theblissprogrammer.amazon.sdk.parsers.FBAFeesReportFileParser
import com.theblissprogrammer.amazon.sdk.parsers.InventoriesReportFileParser
import com.theblissprogrammer.amazon.sdk.parsers.OrdersReportXmlParser
import com.theblissprogrammer.amazon.sdk.parsers.ProductsReportFileParser
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.enums.ReportType
import com.theblissprogrammer.amazon.sdk.extensions.processReport
import com.theblissprogrammer.amazon.sdk.network.APISessionType

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/
class ReportsNetworkStore(val apiSession: APISessionType): ReportsStore {

    override fun <T> fetchReportAsync(request: ReportModels.Request): DeferredResult<List<T>> {
        return coroutineNetwork {
            processReport(request) {
                when (request.type) {
                    ReportType.AllListings, ReportType.InventoryMFN -> {
                        ProductsReportFileParser().parse(it)
                    }
                    ReportType.OrderByUpdateDate, ReportType.OrderByOrderDate -> {
                        OrdersReportXmlParser().parse(it)
                    }
                    ReportType.InventoryAFN -> {
                        InventoriesReportFileParser().parse(it)
                    }
                    ReportType.FBAFees -> {
                        FBAFeesReportFileParser().parse(it)
                    }
                    ReportType.Unknown -> {
                        listOf<Void>()
                    }
                } as List<T>
            }
        }
    }
}