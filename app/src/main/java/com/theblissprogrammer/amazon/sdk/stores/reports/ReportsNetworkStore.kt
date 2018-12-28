package com.theblissprogrammer.amazon.sdk.stores.reports

import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetwork
import com.theblissprogrammer.amazon.sdk.stores.orders.models.Order
import com.theblissprogrammer.amazon.sdk.extensions.fetchReport
import com.theblissprogrammer.amazon.sdk.stores.fbaFees.models.FBAFeeType
import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
import com.theblissprogrammer.amazon.sdk.parsers.FBAFeesReportFileParser
import com.theblissprogrammer.amazon.sdk.parsers.InventoriesReportFileParser
import com.theblissprogrammer.amazon.sdk.parsers.OrdersReportXmlParser
import com.theblissprogrammer.amazon.sdk.parsers.ProductsReportFileParser
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.network.APISessionType
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product
import kotlinx.coroutines.Deferred

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/
class ReportsNetworkStore(val apiSession: APISessionType): ReportsStore {

    override fun fetchOrderReport(request: ReportModels.Request, completion: CompletionResponse<List<Order>>):
            Deferred<Result<List<Order>>> {
        return coroutineNetwork<List<Order>> {
            fetchReport(request, completion) {
                OrdersReportXmlParser().parse(it)
            }
        }
    }

    override fun fetchInventoryReport(request: ReportModels.Request, completion: CompletionResponse<List<Inventory>>):
            Deferred<Result<List<Inventory>>> {
        return coroutineNetwork<List<Inventory>> {
            fetchReport(request, completion) {
                InventoriesReportFileParser().parse(it)
            }
        }
    }

    override fun fetchProductReport(request: ReportModels.Request, completion: CompletionResponse<List<Product>>):
            Deferred<Result<List<Product>>> {
        return coroutineNetwork<List<Product>> {
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