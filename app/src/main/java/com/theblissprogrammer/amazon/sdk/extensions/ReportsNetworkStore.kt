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
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.common.initDataError
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.logging.LogHelper

/**
 * Created by ahmedsaad on 2018-08-07.
 * Copyright (c) 2018. All rights reserved.
 **/


internal fun <T> ReportsNetworkStore.fetchReport(request: ReportModels.Request, completion: CompletionResponse<List<T>>, call: (data: String) -> List<T>): Result<List<T>> {
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
        completion(report)

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
        completion(report)

        val reportValues = report.value

        if (reportValues != null)
            response.addAll(reportValues)
    }

    return success(response)
}

internal fun ReportsNetworkStore.requestReport(request: ReportModels.Request): Result<String> {
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
            failure(exception)
        } else {
            failure(DataError.UnknownReason(null))
        }
    }

    return try {
        // Parse response data
        val payload = RequestReportXmlParser().parse(response.value?.data ?: "")?.requestID

        success(payload)
    } catch(e: Exception) {
        LogHelper.e(messages = *arrayOf("An error occurred while parsing report: " +
                "${e.localizedMessage ?: ""}."))
        failure(DataError.ParseFailure(e))
    }
}

internal fun ReportsNetworkStore.fetchReportRequest(request: ReportModels.ReportRequest): Result<List<RequestReport>> {
    val response = apiSession.request(
            router = APIRouter.ReportRequestList(request = request)
    )

    // Handle errors
    if (response.value == null || !response.isSuccess) {
        val error = response.error

        return if (error != null) {
            val exception = initDataError(response.error)
            LogHelper.e(messages = *arrayOf("An error occurred while fetching report: " +
                    "${error.description}."))
            failure(exception)
        } else {
            failure(DataError.UnknownReason(null))
        }
    }

    return try {
        // Parse response data
        val payload = ReportRequestListXmlParser().parse(response.value?.data ?: "")

        success(payload)
    } catch(e: Exception) {
        LogHelper.e(messages = *arrayOf("An error occurred while parsing report: " +
                "${e.localizedMessage ?: ""}."))
        failure(DataError.ParseFailure(e))
    }
}

internal fun <T> ReportsNetworkStore.readReport(id: String, call: (data: String) -> T): Result<T> {
    val response = apiSession.request(
            router = APIRouter.ReadReport(id)
    )

    // Handle errors
    if (response.value == null || !response.isSuccess) {
        val error = response.error

        return if (error != null) {
            val exception = initDataError(response.error)
            LogHelper.e(messages = *arrayOf("An error occurred while fetching report: " +
                    "${error.description}."))
            failure(exception)
        } else {
            failure(DataError.UnknownReason(null))
        }
    }

    return try {
        // Parse response data
        val payload = call(response.value?.data ?: "")
        success(payload)
    } catch(e: Exception) {
        LogHelper.e(messages = *arrayOf("An error occurred while parsing report: " +
                "${e.localizedMessage ?: ""}."))
        failure(DataError.ParseFailure(e))
    }
}