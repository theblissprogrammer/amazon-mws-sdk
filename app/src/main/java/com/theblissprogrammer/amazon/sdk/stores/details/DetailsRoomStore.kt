package com.theblissprogrammer.amazon.sdk.stores.details

import com.theblissprogrammer.amazon.sdk.common.DeferredLiveResult
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetwork
import com.theblissprogrammer.amazon.sdk.extensions.coroutineRoom
import com.theblissprogrammer.amazon.sdk.stores.common.insertOrUpdate
import com.theblissprogrammer.amazon.sdk.stores.details.models.Detail
import com.theblissprogrammer.amazon.sdk.stores.details.models.ProductDetail

/**
 * Created by ahmed.saad on 2019-03-04.
 * Copyright © 2019. All rights reserved.
 */

class DetailsRoomStore(val detailDao: DetailDAO?): DetailsCacheStore {
    override fun fetchAsync(request: List<String>): DeferredLiveResult<Array<Detail>> {
        return coroutineRoom<Array<Detail>> {

            val items = detailDao?.fetch(request.toTypedArray())
                LiveResult.success(items)
        }
    }

    override fun fetchProductDetailAsync(request: List<String>): DeferredLiveResult<List<ProductDetail>> {
        return coroutineRoom<List<ProductDetail>> {

            val items = detailDao?.fetchProductDetails()
            LiveResult.success(items)
        }
    }

    override fun createOrUpdateAsync(request: Detail): DeferredLiveResult<Detail> {
        return coroutineRoom<Detail> {

            detailDao?.insertOrUpdate(request)

            val item = detailDao?.fetch(asin = request.asin)

                LiveResult.success(item)
        }
    }

    override fun createOrUpdateAsync(vararg detail: Detail): DeferredResult<Void> {
        return coroutineNetwork<Void> {
            detailDao?.insert(*detail)
            Result.success()
        }
    }

}