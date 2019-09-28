package com.theblissprogrammer.amazon.sdk.stores.products

import com.theblissprogrammer.amazon.sdk.common.DeferredLiveResult
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetworkAsync
import com.theblissprogrammer.amazon.sdk.extensions.coroutineRoomAsync
import com.theblissprogrammer.amazon.sdk.stores.common.insertOrUpdate
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product

/**
 * Created by ahmed.saad on 2018-12-19.
 * Copyright © 2018. All rights reserved.
 */
class ProductsRoomStore(val productDAO: ProductDAO?): ProductsCacheStore {

    override fun fetchAsync(request: List<String>): DeferredLiveResult<Array<Product>> {
        return coroutineRoomAsync<Array<Product>> {

            val items = productDAO?.fetch(request.toTypedArray())
            LiveResult.success(items)
        }
    }

    override fun createOrUpdateAsync(request: Product): DeferredLiveResult<Product> {
        return coroutineRoomAsync<Product> {

            productDAO?.insertOrUpdate(request)

            val item = productDAO?.fetch(sku = request.sku)

            if (item == null) {
                LiveResult.failure(DataError.NonExistent)
            } else {
                LiveResult.success(item)
            }
        }
    }

    override fun createOrUpdateAsync(vararg products: Product): DeferredResult<Void> {
        return coroutineNetworkAsync<Void> {
            productDAO?.insert(*products)
            Result.success()
        }
    }

}