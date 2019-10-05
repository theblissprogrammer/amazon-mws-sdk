package com.theblissprogrammer.amazon.sdk.extensions

import com.theblissprogrammer.amazon.sdk.enums.ReportStatus
import com.theblissprogrammer.amazon.sdk.network.APIRouter
import com.theblissprogrammer.amazon.sdk.parsers.ReportRequestListXmlParser
import com.theblissprogrammer.amazon.sdk.stores.reports.ReportsNetworkStore
import com.theblissprogrammer.amazon.sdk.parsers.RequestReportXmlParser
import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
import com.theblissprogrammer.amazon.sdk.stores.reports.models.RequestReport
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.failure
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.success
import com.theblissprogrammer.amazon.sdk.common.initDataError
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.logging.LogHelper

/**
 * Created by ahmedsaad on 2018-08-07.
 * Copyright (c) 2018. All rights reserved.
 **/


/*internal fun <T> ReportsNetworkStore.processReport(request: ReportModels.Request, call: (data: String) -> List<T>): Result<List<T>> {
    val response: ArrayList<T> = arrayListOf()

    val reportRequestList = ReportModels.ReportRequest(
            types = listOf(request.type),
            statuses = listOf(ReportStatus._DONE_)
    )

    var reportRequest = fetchReportRequest(reportRequestList)

    var value = reportRequest.value?.filter {
        if (request.date == null) true
        else
        it.endDate?.after(request.date) == true && it.startDate?.before(request.date) == true
    }?.sortedByDescending { it.endDate }?.firstOrNull()

    if (value?.reportID != null) {
        val report = readReport(value.reportID ?: "", call)

        val prevReportValues = report.value

        if (prevReportValues != null)
            response.addAll(prevReportValues)
    }

    // Set the new date if partial report already exists on amazon
    request.date = value?.endDate ?: request.date

    val requestReport = requestReport(request)

    val requestID = requestReport.value
    // Handle errors
    if (requestID == null || !requestReport.isSuccess) {
        return failure(requestReport.error)
    }

    // Set the requestID to fetchSellerAsync
    reportRequestList.ids = arrayListOf(requestID)
    reportRequestList.statuses = listOf() // Clear done status

    Thread.sleep(1000 * 4) // Sleep for 4 sec
    reportRequest = fetchReportRequest(reportRequestList)

    value = reportRequest.value?.firstOrNull()

    // Handle errors
    while (value == null || !reportRequest.isSuccess) {
        Thread.sleep(1000 * 20) // Sleep for 20 sec
        reportRequest = fetchReportRequest(reportRequestList)
    }

    var status = value.status ?: ReportStatus._CANCELLED_

    while (status == ReportStatus._SUBMITTED_ || status == ReportStatus._IN_PROGRESS_) {
        Thread.sleep(1000 * 4) // Sleep for 4 sec
        reportRequest = fetchReportRequest(reportRequestList)

        value = reportRequest.value?.firstOrNull()

        // Handle errors
        while (value == null || !reportRequest.isSuccess) {
            Thread.sleep(1000 * 30) // Sleep for 30 sec
            reportRequest = fetchReportRequest(reportRequestList)
        }

        status = value.status ?: ReportStatus._CANCELLED_
    }

    val reportID = value?.reportID

    // Handle errors
    if (reportID == null || !reportRequest.isSuccess) {
        return if (response.isEmpty()) failure(reportRequest.error) else success(response)
    }

    if (status == ReportStatus._DONE_) {
        val report = readReport(reportID, call)

        val reportValues = report.value

        if (reportValues != null)
            response.addAll(reportValues)
    }

    return success(response)
}
*/
