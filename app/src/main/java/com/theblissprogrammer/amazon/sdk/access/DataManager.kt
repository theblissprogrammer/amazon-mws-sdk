package com.theblissprogrammer.amazon.sdk.access

import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.common.LiveCompletionResponse
import com.theblissprogrammer.amazon.sdk.dependencies.HasDependencies
import com.theblissprogrammer.amazon.sdk.stores.orders.models.ExpandedOrderType
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderModels
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.SellerModels

/**
 * Created by ahmed.saad on 2018-12-07.
 * Copyright © 2018. All rights reserved.
 */
internal class DataManager: DataManagerInterface,
    HasDependencies {

    private val sellersWorker = dependencies.resolveSellersWorker

    override suspend fun fetchSellerAsync(request: SellerModels.Request, completion: LiveCompletionResponse<Seller>) {
        sellersWorker.fetchSellerAsync(request, completion)
    }

    override suspend fun fetchCurrentSellerAsync(completion: LiveCompletionResponse<Seller>) {
        sellersWorker.fetchCurrentSellerAsync(completion)
    }

    override fun fetchSeller(request: SellerModels.Request, completion: CompletionResponse<Seller>) {
        sellersWorker.fetchSeller(request, completion)
    }

    override fun fetchCurrentSeller(completion: CompletionResponse<Seller>) {
        sellersWorker.fetchCurrentSeller(completion)
    }

    override fun fetch(request: OrderModels.Request, completion: CompletionResponse<List<ExpandedOrderType>>) {
        // TODO: ("fetch not implemented")
    }

    override fun fetchOldestOrder(completion: CompletionResponse<ExpandedOrderType>) {
        // TODO: ("fetchOldestOrder not implemented")
    }

    override fun fetchOrder(by: OrderModels.SearchRequest, completion: CompletionResponse<ExpandedOrderType>) {
        // TODO: ("fetchOrder not implemented")
    }
}