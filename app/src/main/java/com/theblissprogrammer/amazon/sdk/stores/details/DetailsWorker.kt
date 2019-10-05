package com.theblissprogrammer.amazon.sdk.stores.details

import com.theblissprogrammer.amazon.sdk.common.LiveCompletionResponse
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.stores.details.models.Detail
import com.theblissprogrammer.amazon.sdk.stores.details.models.ProductDetail

/**
 * Created by ahmed.saad on 2019-03-04.
 * Copyright © 2019. All rights reserved.
 */
class DetailsWorker(val store: DetailsStore,
                    val cacheStore: DetailsCacheStore): DetailsWorkerType {

    /*override suspend fun fetchAsync(request: List<String>, completion: LiveCompletionResponse<Array<Detail>>) {
        val cache = cacheStore.fetchProductDetailAsync(request = request).await()

        request.forEach {
            val details = store.fetchAsync(it).await()

            if (!details.isSuccess || details.value == null) {
                return@forEach LogHelper.e(messages = *arrayOf("Error occurred while retrieving details for $it: ${details.error ?: ""}"))
            }

            val savedElement = this.cacheStore.createOrUpdateAsync(details.value).await()

            if (!savedElement.isSuccess) {
                return@forEach LogHelper.e(
                    messages = *arrayOf(
                        "Could not save updated details locally for $it" +
                                " from remote storage: ${savedElement.error?.localizedMessage ?: ""}"
                    )
                )
            }
        }

        completion(cache)
    }*/

    override suspend fun fetchProductDetails(asins: List<String>, completion: LiveCompletionResponse<List<ProductDetail>>) {
        val cache = cacheStore.fetchProductDetailAsync(request = asins).await()
        completion(cache)
    }
}