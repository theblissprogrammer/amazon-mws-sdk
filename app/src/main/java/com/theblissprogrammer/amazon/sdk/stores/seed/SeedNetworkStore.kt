package com.theblissprogrammer.amazon.sdk.stores.seed

import com.theblissprogrammer.amazon.sdk.enums.ReportType
import com.theblissprogrammer.amazon.sdk.stores.reports.ReportsNetworkStore
import com.theblissprogrammer.amazon.sdk.stores.seed.models.SeedPayload
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.success
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ahmedsaad on 2018-08-23.
 * Copyright (c) 2018. All rights reserved.
 **/
class SeedNetworkStore(val reportsStore: ReportsNetworkStore,
                       val preferencesWorker: PreferencesWorkerType): SeedStore {

    val reports = listOf(
            ReportType.OrderByUpdateDate,
            ReportType.InventoryAFN,
            ReportType.AllListings,
            ReportType.FBAFees
    )

    override fun fetchPayload(newerThan: Date?, completion: CompletionResponse<SeedPayload>) {
        val payload = SeedPayload()

        /*var reportRequestList = ReportModels.ReportRequest(
                requestFrom = newerThan,
                types = reports,
                statuses = listOf(ReportStatus._DONE_)
        )

        var reportRequest = reportsStore.fetchReportRequest(reportRequestList)

        reportRequest.value?.groupBy { it.type }?.mapNotNull {
            it.value.sortedByDescending { it.endDate }.firstOrNull {
                if (newerThan == null) true
                else
                    it.endDate?.after(newerThan) == true && it.startDate?.before(newerThan) == true
            }
        }?.forEach {requestReport ->
            if (requestReport.reportID != null) {
                reportsStore.readReport(requestReport.reportID) {
                    when (requestReport.type) {
                        ReportType.OrderByUpdateDate -> {
                            val orders = OrdersReportXmlParser().parse(it)
                            payload.orders = orders
                            orders
                        }
                        ReportType.InventoryAFN -> {
                            val inventories = InventoriesReportFileParser().parse(it)
                            payload.inventories = inventories
                            inventories
                        }
                        ReportType.AllListings -> {
                            val products = ProductsReportFileParser().parse(it)
                            payload.products = products
                            products
                        }
                        ReportType.FBAFees -> {
                            val fbaFees = FBAFeesReportFileParser().parse(it)
                            payload.fbaFees = fbaFees
                            fbaFees
                        }
                        else -> {
                            listOf()
                        }
                    }
                }
            }
        }

        if (!payload.isEmpty) {
            completion(success(payload))
        }

        val requestIDs = reports.mapNotNull {
            val request = ReportModels.Request(
                    type = it,
                    date = if (it != ReportType.InventoryAFN && it != ReportType.FBAFees) newerThan else null,
                    marketplaces = SyncRoomStore.getSellerMarketplaces(preferencesWorker) ?: listOf(MarketplaceType.US)

            )

            val requestReport = reportsStore.requestReport(request)

            requestReport.value
        }

        reportRequestList = ReportModels.ReportRequest(
                ids = ArrayList(requestIDs)
        )

        while (reportRequestList.ids.isNotEmpty()) {
            Thread.sleep(1000 * 4) // Sleep for 4 secs

            reportRequest = reportsStore.fetchReportRequest(reportRequestList)

            reportRequest.value?.forEach { requestReport ->
                if (requestReport.reportID != null && requestReport.status == ReportStatus._DONE_) {
                    reportsStore.readReport(requestReport.reportID) {
                        when (requestReport.type) {
                            ReportType.OrderByUpdateDate -> {
                                val orders = OrdersReportXmlParser().parse(it)
                                payload.orders = orders
                                orders
                            }
                            ReportType.InventoryAFN -> {
                                val inventories = InventoriesReportFileParser().parse(it)
                                payload.inventories = inventories
                                inventories
                            }
                            ReportType.AllListings -> {
                                val products = ProductsReportFileParser().parse(it)
                                payload.products = products
                                products
                            }
                            ReportType.FBAFees -> {
                                val fbaFees = FBAFeesReportFileParser().parse(it)
                                payload.fbaFees = fbaFees
                                fbaFees
                            }
                            else -> {
                                listOf()
                            }
                        }
                    }
                }

                if (listOf(ReportStatus._CANCELLED_, ReportStatus._DONE_NO_DATA_, ReportStatus._DONE_)
                                .contains(requestReport.status)) {
                    reportRequestList.ids.remove(requestReport.requestID)
                }
            }
        }*/


        completion(success(payload))
    }
}