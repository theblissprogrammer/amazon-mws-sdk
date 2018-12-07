package com.theblissprogrammer.amazon.sdk.stores.sellers

import com.theblissprogrammer.amazon.sdk.common.LiveDataResult
import com.theblissprogrammer.amazon.sdk.common.LiveDataResult.Companion.failure
import com.theblissprogrammer.amazon.sdk.common.LiveDataResult.Companion.success
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetwork
import com.theblissprogrammer.amazon.sdk.extensions.coroutineRoom
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.SellerModels
import kotlinx.coroutines.Deferred

/**
 * Created by ahmedsaad on 2018-08-04.
 * Copyright (c) 2018. All rights reserved.
 **/
class SellersRoomStore(val sellerDao: SellerDAO?): SellersCacheStore {

    override fun fetch(request: SellerModels.Request): Deferred<LiveDataResult<Seller>> {
        return coroutineRoom<Seller> {
            val item = sellerDao?.fetch(id = request.id, marketplace = request.marketplace)

            if (item == null) {
                failure(DataError.NonExistent)
            } else {
                success(item)
            }
        }
    }

    override fun createOrUpdate(request: Seller): Deferred<LiveDataResult<Seller>> {
        return coroutineRoom<Seller> {

            sellerDao?.insertOrUpdate(request)

            val item = sellerDao?.fetch(id = request.id, marketplace = request.marketplace)

            if (item == null) {
                failure(DataError.NonExistent)
            } else {
                success(item)
            }
        }
    }

    override fun createOrUpdate(vararg sellers: Seller): Deferred<Result<Void>> {
        return coroutineNetwork<Void> {
            sellerDao?.insert(*sellers)
            Result.success()
        }
    }
}