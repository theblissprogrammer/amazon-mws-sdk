package com.theblissprogrammer.amazon.sdk.stores.sellers

import com.theblissprogrammer.amazon.sdk.common.DeferredLiveResult
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.LiveResult.Companion.failure
import com.theblissprogrammer.amazon.sdk.common.LiveResult.Companion.success
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetwork
import com.theblissprogrammer.amazon.sdk.extensions.coroutineRoom
import com.theblissprogrammer.amazon.sdk.stores.common.insertOrUpdate
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.SellerModels

/**
 * Created by ahmedsaad on 2018-08-04.
 * Copyright (c) 2018. All rights reserved.
 **/
class SellersRoomStore(val sellerDao: SellerDAO?): SellersCacheStore {

    override fun fetch(request: SellerModels.Request): DeferredLiveResult<Seller> {
        return coroutineRoom<Seller> {
            val item = sellerDao?.fetch(id = request.id, marketplace = request.marketplace)

            if (item == null) {
                failure(DataError.NonExistent)
            } else {
                success(item)
            }
        }
    }

    override fun createOrUpdate(request: Seller): DeferredLiveResult<Seller> {
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

    override fun createOrUpdate(vararg sellers: Seller): DeferredResult<Void> {
        return coroutineNetwork<Void> {
            sellerDao?.insert(*sellers)
            Result.success()
        }
    }
}