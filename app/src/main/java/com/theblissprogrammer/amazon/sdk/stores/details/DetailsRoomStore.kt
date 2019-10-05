package com.theblissprogrammer.amazon.sdk.stores.details

import com.theblissprogrammer.amazon.sdk.common.DeferredLiveResult
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetworkAsync
import com.theblissprogrammer.amazon.sdk.extensions.coroutineRoomAsync
import com.theblissprogrammer.amazon.sdk.stores.common.insertOrUpdate
import com.theblissprogrammer.amazon.sdk.stores.details.models.Detail
import com.theblissprogrammer.amazon.sdk.stores.details.models.ProductDetail

/**
 * Created by ahmed.saad on 2019-03-04.
 * Copyright © 2019. All rights reserved.
 */

class DetailsRoomStore(val detailDao: DetailDAO?): DetailsCacheStore {
    fun fetchAsync(request: List<String>): DeferredLiveResult<Array<Detail>> {
        return coroutineRoomAsync<Array<Detail>> {

            val items = detailDao?.fetch(request.toTypedArray())
                LiveResult.success(items)
        }
    }

    override fun fetchProductDetailAsync(request: List<String>): DeferredLiveResult<List<ProductDetail>> {
        return coroutineRoomAsync<List<ProductDetail>> {

            val items = detailDao?.fetchProductDetails(request.toTypedArray())
            LiveResult.success(items)
        }
    }

    override fun createOrUpdateAsync(request: Detail): DeferredLiveResult<Detail> {
        return coroutineRoomAsync<Detail> {

            detailDao?.insertOrUpdate(request)

            val item = detailDao?.fetch(asin = request.asin)

                LiveResult.success(item)
        }
    }

    override fun createOrUpdateAsync(vararg detail: Detail): DeferredResult<Void> {
        return coroutineNetworkAsync<Void> {
            detailDao?.insert(*detail)
            Result.success()
        }
    }

}