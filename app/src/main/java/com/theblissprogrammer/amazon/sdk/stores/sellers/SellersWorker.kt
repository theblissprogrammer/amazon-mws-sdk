package com.theblissprogrammer.amazon.sdk.stores.sellers

import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.SellerModels
import com.theblissprogrammer.amazon.sdk.common.LiveCompletionResponse
import com.theblissprogrammer.amazon.sdk.common.LiveResult.Companion.failure
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys.Companion.sellerID
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller

/**
 * Created by ahmedsaad on 2018-08-04.
 * Copyright (c) 2018. All rights reserved.
 **/
class SellersWorker(val store: SellersStore,
                    val cacheStore: SellersCacheStore,
                    val preferencesWorker: PreferencesWorkerType): SellersWorkerType {

    override suspend fun fetchSellerAsync(request: SellerModels.Request, completion: LiveCompletionResponse<Seller>) {
        // Use cache storage if applicable
        val cache = cacheStore.fetch(request = request).await()

        // Retrieve missing cache data from cloud if applicable
        if (cache.error != null && cache.error === DataError.NonExistent) {
            val response = this.store.fetch(request = request).await()
            val value = response.value
            if (value == null || !response.isSuccess) {
                completion(failure(response.error))
            } else {
                completion(cacheStore.createOrUpdate(value).await())
            }
        }

        // Immediately return local response
        completion(cache)

        val cacheElement = cache.value
        if (cacheElement == null || !cache.isSuccess) {
            return
        }

        // Sync remote updates to cache if applicable
        val response = this.store.fetch(request = request).await()

        // Validate if any updates that needs to be stored
        val element = response.value
        if (element == null || !response.isSuccess) {
            return
        }

        // Update local storage with updated data
        val savedElement = cacheStore.createOrUpdate(element).await()

        if (!savedElement.isSuccess) {
            LogHelper.e(messages = *arrayOf("Could not save updated user locally" +
                    " from remote storage: ${savedElement.error?.localizedMessage ?: ""}"))
        }

        completion(cache)
    }

    override suspend fun fetchCurrentSellerAsync(completion: LiveCompletionResponse<Seller>) {
        val id = preferencesWorker.get(sellerID)
        val marketplace = preferencesWorker.get(DefaultsKeys.marketplace)

        if (id == null || marketplace == null || id.isEmpty() || marketplace.isEmpty()) {
            completion(failure(DataError.Unauthorized))
            return
        }

        val request = SellerModels.Request(
                id = id,
                marketplace = MarketplaceType.valueOf(marketplace)
        )

        fetchSellerAsync(request = request, completion = completion)
    }

    override fun fetchCurrentSeller(completion: CompletionResponse<Seller>) {
        // TODO: ("fetchCurrentSeller not implemented")
    }

    override fun fetchSeller(request: SellerModels.Request, completion: CompletionResponse<Seller>) {
        // TODO: ("fetchSeller not implemented")
    }

}