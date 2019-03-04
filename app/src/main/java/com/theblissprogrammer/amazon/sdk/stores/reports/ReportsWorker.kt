package com.theblissprogrammer.amazon.sdk.stores.reports

import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.enums.ReportType
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.stores.products.ProductsCacheStore
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/
class ReportsWorker(val store: ReportsStore,
                    val productCacheStore: ProductsCacheStore): ReportsWorkerType {

    override suspend fun <T> fetchReport(request: ReportModels.Request, completion: CompletionResponse<List<T>>) {
        if (request.type == ReportType.Unknown) return completion(Result.failure(DataError.BadRequest))

        val result = store.fetchReportAsync<T>(request).await()

        when (request.type) {
            ReportType.AllListings -> {
                val products = result.value as? List<Product>

                if (products != null)
                    this.productCacheStore.createOrUpdateAsync(*products.toTypedArray()).await()
            }
            else ->  {}
        }

        completion(result)
    }
}