package com.theblissprogrammer.amazon.sdk.stores.sellers

import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.SellerModels
import com.theblissprogrammer.amazon.sdk.common.LiveCompletionResponse
import com.theblissprogrammer.amazon.sdk.common.LiveResourceResponse
import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.common.LiveResult.Companion.failure
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys.Companion.sellerID
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.coroutineOnIO
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.network.NetworkBoundResource
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller

/**
 * Created by ahmedsaad on 2018-08-04.
 * Copyright (c) 2018. All rights reserved.
 **/
class SellersWorker(val store: SellersStore,
                    val cacheStore: SellersCacheStore,
                    val preferencesWorker: PreferencesWorkerType): SellersWorkerType {

    override fun fetchSellersAsync(request: SellerModels.Request?, completion: LiveResourceResponse<List<Seller>>) {

        val data = object : NetworkBoundResource<List<Seller>, Seller>() {
            override fun saveCallResult(item: Seller?) {
                if (item != null) {
                    val current = SellerModels.CurrentRequest(
                            id = item.sellerId,
                            marketplace = item.marketplace
                    )

                    item.id = cacheStore.fetchNow(current).value?.id
                    cacheStore.createOrUpdate(item)
                }
            }

            override fun shouldFetch(data: List<Seller>?): Boolean {
                return true // Update the rank
            }

            override fun loadFromDb(): LiveResult<List<Seller>> {
                return cacheStore.fetch(request = request)
            }

            override fun createCall(): Result<Seller> {
                val current = SellerModels.CurrentRequest(
                        id = request?.ids?.first() ?: "",
                        marketplace = request?.marketplaces?.first() ?: MarketplaceType.US
                )

                return store.fetch(request = current)
            }

        }.asLiveData()

        completion(data)
    }

    override fun fetchCurrentSeller(): Result<Seller> {
        val id = preferencesWorker.get(sellerID)
        val marketplace = preferencesWorker.get(DefaultsKeys.marketplace)

        if (id == null || marketplace == null || id.isEmpty() || marketplace.isEmpty()) {
            return Result.failure(DataError.Unauthorized)
        }

        val request = SellerModels.CurrentRequest(
                id = id,
                marketplace = MarketplaceType.valueOf(marketplace)
        )

        return fetchSeller(request)
    }

    override fun fetchSeller(request: SellerModels.CurrentRequest): Result<Seller> {
        return cacheStore.fetchNow(request = request)
    }

}