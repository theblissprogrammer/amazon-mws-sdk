package com.theblissprogrammer.amazon.sdk.stores.sellers

import com.theblissprogrammer.amazon.sdk.common.DeferredLiveResult
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.common.LiveResult.Companion.failure
import com.theblissprogrammer.amazon.sdk.common.LiveResult.Companion.success
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetworkAsync
import com.theblissprogrammer.amazon.sdk.extensions.coroutineRoomAsync
import com.theblissprogrammer.amazon.sdk.stores.common.insertOrUpdate
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.SellerModels

/**
 * Created by ahmedsaad on 2018-08-04.
 * Copyright (c) 2018. All rights reserved.
 **/
class SellersRoomStore(val sellerDao: SellerDAO?): SellersCacheStore {

    override fun fetch(request: SellerModels.Request?): LiveResult<List<Seller>> {
        val item = if (request != null) sellerDao?.fetch(
                ids = request.ids.toTypedArray(),
                marketplaces = request.marketplaces.toTypedArray()
        )
        else sellerDao?.fetchAllSellers()

        return if (item == null) {
            failure(DataError.NonExistent)
        } else {
            success(item)
        }
    }

    override fun fetchNow(request: SellerModels.CurrentRequest): Result<Seller> {
        val item = sellerDao?.fetchSync(id = request.id, marketplace = request.marketplace)

        return if (item == null) {
            Result.failure(DataError.NonExistent)
        } else {
            Result.success(item)
        }
    }

    override fun createOrUpdate(request: Seller) {
        sellerDao?.insertOrUpdate(request)
    }

    override fun createOrUpdate(sellers: List<Seller>) {
        sellerDao?.insertOrUpdate(sellers)
    }
}