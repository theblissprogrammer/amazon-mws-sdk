package com.theblissprogrammer.amazon.sdk.stores.reports

import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.enums.ReportType
import com.theblissprogrammer.amazon.sdk.errors.DataError

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/
class ReportsWorker(val store: ReportsStore): ReportsWorkerType {

    override suspend fun <T> fetchReport(request: ReportModels.Request, completion: CompletionResponse<List<T>>) {
        if (request.type == ReportType.Unknown) return completion(Result.failure(DataError.BadRequest))

        val result = store.fetchReportAsync<T>(request)
        completion(result.await())
    }
}