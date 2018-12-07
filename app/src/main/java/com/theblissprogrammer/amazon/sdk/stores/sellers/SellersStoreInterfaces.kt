package com.theblissprogrammer.amazon.sdk.stores.sellers

import com.theblissprogrammer.amazon.sdk.common.*
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.SellerModels
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller

/**
 * Created by ahmedsaad on 2018-08-03.
 * Copyright (c) 2018. All rights reserved.
 **/
interface SellersStore {
    fun fetch(request: SellerModels.Request): DeferredResult<Seller>
}

interface SellersCacheStore {
    fun fetch(request: SellerModels.Request): DeferredLiveResult<Seller>
    fun createOrUpdate(request: Seller): DeferredLiveResult<Seller>
    fun createOrUpdate(vararg sellers: Seller): DeferredResult<Void>
}

interface SellersWorkerType {
    suspend fun fetchSellerAsync(request: SellerModels.Request, completion: LiveCompletionResponse<Seller>)
    suspend fun fetchCurrentSellerAsync(completion: LiveCompletionResponse<Seller>)

    fun fetchSeller(request: SellerModels.Request, completion: CompletionResponse<Seller>)
    fun fetchCurrentSeller(completion: CompletionResponse<Seller>)
}