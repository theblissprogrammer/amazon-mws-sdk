package com.theblissprogrammer.amazon.sdk.stores.sellers

import com.theblissprogrammer.amazon.sdk.common.*
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.SellerModels
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller

/**
 * Created by ahmedsaad on 2018-08-03.
 * Copyright (c) 2018. All rights reserved.
 **/
interface SellersStore {
    fun fetch(request: SellerModels.CurrentRequest) : Result<Seller>
}

interface SellersCacheStore {
    fun fetch(request: SellerModels.Request?): LiveResult<List<Seller>>
    fun fetchNow(request: SellerModels.CurrentRequest): Result<Seller>
    fun createOrUpdate(request: Seller)
    fun createOrUpdate(sellers: List<Seller>)
}

interface SellersWorkerType {
    fun fetchSellersAsync(request: SellerModels.Request? = null, completion: LiveResourceResponse<List<Seller>>)

    fun fetchSeller(request: SellerModels.CurrentRequest): Result<Seller>
    fun fetchCurrentSeller(): Result<Seller>
}