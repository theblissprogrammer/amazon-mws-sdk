package com.theblissprogrammer.amazon.sdk.stores.reports

import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.stores.reports.models.RequestReport

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/
interface ReportsStore {
    fun <T> fetchReportAsync(request: ReportModels.Request): DeferredResult<List<T>>
    fun requestReport(request: ReportModels.Request): Result<String>
    fun readReport(request: ReportModels.ReadRequest): Result<Any>
    fun fetchReportRequest(request: ReportModels.ReportRequest): Result<List<RequestReport>>
}

interface ReportsWorkerType {
    suspend fun <T> fetchReport(request: ReportModels.Request, completion: CompletionResponse<List<T>>)
    fun requestReport(request: ReportModels.Request)
    fun processReport(request: ReportModels.ReadRequest)
    fun fetchReportRequest(request: ReportModels.ReportRequest, completion: CompletionResponse<List<RequestReport>>)
}