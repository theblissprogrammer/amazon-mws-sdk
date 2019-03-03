package com.theblissprogrammer.amazon.sdk.stores.reports

import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.common.DeferredResult

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/
interface ReportsStore {
    fun <T> fetchReportAsync(request: ReportModels.Request): DeferredResult<List<T>>
}

interface ReportsWorkerType {
    suspend fun <T> fetchReport(request: ReportModels.Request, completion: CompletionResponse<List<T>>)
}