package com.theblissprogrammer.amazon.sdk.stores.reports

import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.asResource
import com.theblissprogrammer.amazon.sdk.enums.ReportType
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.coroutineBackgroundAsync
import com.theblissprogrammer.amazon.sdk.extensions.coroutineOnIO
import com.theblissprogrammer.amazon.sdk.parsers.FbaMYIInventoriesReportModel
import com.theblissprogrammer.amazon.sdk.parsers.InventoriesReportFileModel
import com.theblissprogrammer.amazon.sdk.parsers.OrdersReportXmlModel
import com.theblissprogrammer.amazon.sdk.stores.inventory.InventoryCacheStore
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.InventoryDetail
import com.theblissprogrammer.amazon.sdk.stores.orderItems.OrderItemsCacheStore
import com.theblissprogrammer.amazon.sdk.stores.orders.OrdersCacheStore
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderDetail
import com.theblissprogrammer.amazon.sdk.stores.products.ProductsCacheStore
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product
import com.theblissprogrammer.amazon.sdk.stores.reports.models.RequestReport

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/
class ReportsWorker(val store: ReportsStore,
                    val ordersCacheStore: OrdersCacheStore,
                    val itemsCacheStore: OrderItemsCacheStore,
                    val productCacheStore: ProductsCacheStore,
                    val inventoryCacheStore: InventoryCacheStore): ReportsWorkerType {

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

    override fun requestReport(request: ReportModels.Request) {
        coroutineOnIO {
            coroutineBackgroundAsync {
                store.requestReport(request)
            }.await()
        }
    }

    override fun processReport(request: ReportModels.ReadRequest) {
        coroutineOnIO {
            val resource = coroutineBackgroundAsync {
                store.readReport(request).asResource()
            }.await()

            if (resource.data == null) return@coroutineOnIO

            when (request.type) {
                ReportType.OrderByUpdateDate, ReportType.OrderByOrderDate -> {
                    val list = resource.data as? OrdersReportXmlModel ?: return@coroutineOnIO

                    ordersCacheStore.createOrUpdate(list.orders)
                    itemsCacheStore.createOrUpdate(list.items)
                }
                ReportType.FBAMYIInventory -> {
                    val list = resource.data as? FbaMYIInventoriesReportModel ?: return@coroutineOnIO
                    val inventories = list.inventories.toList()
                    val products = list.products.toList()

                    inventoryCacheStore.createOrUpdate(inventories)
                    productCacheStore.createOrUpdate(products)
                }
                ReportType.InventoryAFN -> {
                    val list = resource.data as? InventoriesReportFileModel ?: return@coroutineOnIO

                    inventoryCacheStore.createOrUpdate(list.inventories)
                }
                else -> {}
            }
        }
    }

    override fun fetchReportRequest(request: ReportModels.ReportRequest, completion: CompletionResponse<List<RequestReport>>) {
        coroutineOnIO {
            val requests = coroutineBackgroundAsync {
                store.fetchReportRequest(request)
            }.await()

            completion(requests)
        }
    }
}