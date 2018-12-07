package com.theblissprogrammer.amazon.sdk.stores.sellers

import androidx.lifecycle.LiveData
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.SellerModels
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.common.LiveDataCompletionResponse
import com.theblissprogrammer.amazon.sdk.common.LiveDataResult
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller
import kotlinx.coroutines.Deferred

/**
 * Created by ahmedsaad on 2018-08-03.
 * Copyright (c) 2018. All rights reserved.
 **/
interface SellersStore {
    fun fetch(request: SellerModels.Request): Deferred<Result<Seller>>
}

interface SellersCacheStore {
    fun fetch(request: SellerModels.Request): Deferred<LiveDataResult<Seller>>
    fun createOrUpdate(request: Seller): Deferred<LiveDataResult<Seller>>
    fun createOrUpdate(vararg sellers: Seller): Deferred<Result<Void>>
}

interface SellersWorkerType {
    suspend fun fetch(request: SellerModels.Request, completion: LiveDataCompletionResponse<Seller>)
    suspend fun fetchCurrent(completion: LiveDataCompletionResponse<Seller>)
}