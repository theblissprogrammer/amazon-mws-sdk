package com.theblissprogrammer.amazon.sdk.stores.reports

import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetwork
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderType
import com.theblissprogrammer.amazon.sdk.extensions.fetchReport
import com.theblissprogrammer.amazon.sdk.stores.fbaFees.models.FBAFeeType
import com.theblissprogrammer.amazon.sdk.stores.inventories.models.InventoryType
import com.theblissprogrammer.amazon.sdk.stores.products.models.ProductType
import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
import com.theblissprogrammer.amazon.sdk.stores.reports.parsers.FBAFeesReportFileParser
import com.theblissprogrammer.amazon.sdk.stores.reports.parsers.InventoriesReportFileParser
import com.theblissprogrammer.amazon.sdk.stores.reports.parsers.OrdersReportXmlParser
import com.theblissprogrammer.amazon.sdk.stores.reports.parsers.ProductsReportFileParser
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.network.APISessionType
import kotlinx.coroutines.Deferred

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/
class ReportsNetworkStore(val apiSession: APISessionType): ReportsStore {

    override fun fetchOrderReport(request: ReportModels.Request, completion: CompletionResponse<List<OrderType>>):
            Deferred<Result<List<OrderType>>> {
        return coroutineNetwork<List<OrderType>> {
            fetchReport(request, completion) {
                OrdersReportXmlParser().parse(it)
            }
        }
    }

    override fun fetchInventoryReport(request: ReportModels.Request, completion: CompletionResponse<List<InventoryType>>):
            Deferred<Result<List<InventoryType>>> {
        return coroutineNetwork<List<InventoryType>> {
            fetchReport(request, completion) {
                InventoriesReportFileParser().parse(it)
            }
        }
    }

    override fun fetchProductReport(request: ReportModels.Request, completion: CompletionResponse<List<ProductType>>):
            Deferred<Result<List<ProductType>>> {
        return coroutineNetwork<List<ProductType>> {
            fetchReport(request, completion) {
                ProductsReportFileParser().parse(it)
            }
        }
    }

    override fun fetchFBAFeeReport(request: ReportModels.Request, completion: CompletionResponse<List<FBAFeeType>>):
            Deferred<Result<List<FBAFeeType>>> {
        return coroutineNetwork<List<FBAFeeType>> {
            fetchReport(request, completion) {
                FBAFeesReportFileParser().parse(it)
            }
        }
    }
}