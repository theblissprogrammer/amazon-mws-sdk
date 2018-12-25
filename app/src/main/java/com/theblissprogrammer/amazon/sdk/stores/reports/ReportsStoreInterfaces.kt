package com.theblissprogrammer.amazon.sdk.stores.reports

import com.theblissprogrammer.amazon.sdk.stores.fbaFees.models.FBAFeeType
import com.theblissprogrammer.amazon.sdk.stores.inventories.models.InventoryType
import com.theblissprogrammer.amazon.sdk.stores.orders.models.Order
import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product
import kotlinx.coroutines.Deferred

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/
interface ReportsStore {
    fun fetchOrderReport(request: ReportModels.Request, completion: CompletionResponse<List<Order>>):
            Deferred<Result<List<Order>>>
    fun fetchInventoryReport(request: ReportModels.Request, completion: CompletionResponse<List<InventoryType>>):
            Deferred<Result<List<InventoryType>>>
    fun fetchProductReport(request: ReportModels.Request, completion: CompletionResponse<List<Product>>):
            Deferred<Result<List<Product>>>
    fun fetchFBAFeeReport(request: ReportModels.Request, completion: CompletionResponse<List<FBAFeeType>>):
            Deferred<Result<List<FBAFeeType>>>
}

interface ReportsWorkerType: ReportsStore