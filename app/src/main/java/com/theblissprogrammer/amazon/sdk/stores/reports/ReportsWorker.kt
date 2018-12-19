package com.theblissprogrammer.amazon.sdk.stores.reports

import com.theblissprogrammer.amazon.sdk.stores.fbaFees.models.FBAFeeType
import com.theblissprogrammer.amazon.sdk.stores.inventories.models.InventoryType
import com.theblissprogrammer.amazon.sdk.stores.orders.models.Order
import com.theblissprogrammer.amazon.sdk.stores.products.models.ProductType
import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import kotlinx.coroutines.Deferred

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/
class ReportsWorker(val store: ReportsStore): ReportsWorkerType {
    override fun fetchOrderReport(request: ReportModels.Request, completion: CompletionResponse<List<Order>>):
            Deferred<Result<List<Order>>> {
        return store.fetchOrderReport(request, completion = completion)
    }

    override fun fetchInventoryReport(request: ReportModels.Request, completion: CompletionResponse<List<InventoryType>>):
            Deferred<Result<List<InventoryType>>> {
        return store.fetchInventoryReport(request, completion = completion)
    }

    override fun fetchProductReport(request: ReportModels.Request, completion: CompletionResponse<List<ProductType>>):
            Deferred<Result<List<ProductType>>> {
        return store.fetchProductReport(request, completion = completion)
    }

    override fun fetchFBAFeeReport(request: ReportModels.Request, completion: CompletionResponse<List<FBAFeeType>>):
            Deferred<Result<List<FBAFeeType>>> {
        return store.fetchFBAFeeReport(request, completion = completion)
    }
}