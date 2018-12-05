package com.theblissprogrammer.amazon.sdk.stores.sellers

import com.theblissprogrammer.amazon.sdk.stores.sellers.models.SellerModels
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller
import kotlinx.coroutines.Deferred

/**
 * Created by ahmedsaad on 2018-08-03.
 * Copyright (c) 2018. All rights reserved.
 **/
interface SellersStore {
    fun fetch(request: SellerModels.Request): Deferred<Result<Seller>>
}

interface SellersCacheStore: SellersStore {
    fun createOrUpdate(request: Seller): Deferred<Result<Seller>>
    fun createOrUpdate(vararg sellers: Seller): Deferred<Result<Void>>
}

interface SellersWorkerType {
    suspend fun fetch(request: SellerModels.Request, completion: CompletionResponse<Seller>)
    suspend fun fetchCurrent(completion: CompletionResponse<Seller>)
}