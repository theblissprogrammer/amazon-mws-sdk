package com.theblissprogrammer.amazon.sdk.stores.reports

import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetworkAsync
import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.initDataError
import com.theblissprogrammer.amazon.sdk.enums.ReportType
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.network.APIRouter
import com.theblissprogrammer.amazon.sdk.network.APISessionType
import com.theblissprogrammer.amazon.sdk.parsers.*

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/
class ReportsNetworkStore(val apiSession: APISessionType): ReportsStore {

    override fun <T> fetchReportAsync(request: ReportModels.Request): DeferredResult<List<T>> {
        return coroutineNetworkAsync {
            Result.success(listOf<T>())
        }
    }

    override fun requestReport(request: ReportModels.Request): Result<String> {
        val response = apiSession.request(
                router = APIRouter.RequestReport(request = request)
        )

        // Handle errors
        if (response.value == null || !response.isSuccess) {
            val error = response.error

            return if (error != null) {
                val exception = initDataError(response.error)
                LogHelper.e(messages = *arrayOf("An error occurred while fetching report: " +
                        "${error.description}."))
                Result.failure(exception)
            } else {
                Result.failure(DataError.UnknownReason(null))
            }
        }

        return try {
            // Parse response data
            val payload = RequestReportXmlParser().parse(response.value.data)?.requestID

            Result.success(payload)
        } catch(e: Exception) {
            LogHelper.e(messages = *arrayOf("An error occurred while parsing report: " +
                    "${e.localizedMessage ?: ""}."))
            Result.failure(DataError.ParseFailure(e))
        }
    }

    override fun readReport(request: ReportModels.ReadRequest): Result<Any> {
        val response = apiSession.request(
                router = APIRouter.ReadReport(request.id)
        )

        // Handle errors
        if (response.value == null || !response.isSuccess) {
            val error = response.error

            return if (error != null) {
                val exception = initDataError(response.error)
                LogHelper.e(messages = *arrayOf("An error occurred while fetching report: " +
                        "${error.description}."))
                Result.failure(exception)
            } else {
                Result.failure(DataError.UnknownReason(null))
            }
        }

        return try {
            // Parse response data
            val data = response.value.data

            val payload = when (request.type) {
                ReportType.AllListings, ReportType.InventoryMFN -> {
                    ProductsReportFileParser().parse(data)
                }
                ReportType.OrderByUpdateDate, ReportType.OrderByOrderDate -> {
                    OrdersReportXmlParser().parse(data)
                }
                ReportType.InventoryAFN -> {
                    InventoriesReportFileParser().parse(data)
                }
                ReportType.FBAMYIInventory -> {
                    FbaMYIInventoriesReportFileParser().parse(data)
                }
                ReportType.FBAFees -> {
                    FBAFeesReportFileParser().parse(data)
                }
                ReportType.Unknown -> {
                    listOf<Void>()
                }
            }

            Result.success(payload)
        } catch(e: Exception) {
            LogHelper.e(messages = *arrayOf("An error occurred while parsing report: " +
                    "${e.localizedMessage ?: ""}."))
            Result.failure(DataError.ParseFailure(e))
        }
    }


}