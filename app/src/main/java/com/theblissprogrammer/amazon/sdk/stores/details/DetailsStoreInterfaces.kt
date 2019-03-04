package com.theblissprogrammer.amazon.sdk.stores.details

import com.theblissprogrammer.amazon.sdk.common.DeferredLiveResult
import com.theblissprogrammer.amazon.sdk.common.LiveCompletionResponse
import com.theblissprogrammer.amazon.sdk.stores.common.CommonCacheStore
import com.theblissprogrammer.amazon.sdk.stores.common.CommonStore
import com.theblissprogrammer.amazon.sdk.stores.common.CommonWorkerType
import com.theblissprogrammer.amazon.sdk.stores.details.models.Detail
import com.theblissprogrammer.amazon.sdk.stores.details.models.ProductDetail

/**
 * Created by ahmed.saad on 2019-03-04.
 * Copyright © 2019. All rights reserved.
 */

interface DetailsStore: CommonStore<Detail, String>

interface DetailsCacheStore: CommonCacheStore<Detail, List<String>> {
    fun fetchProductDetailAsync(request: List<String>): DeferredLiveResult<List<ProductDetail>>
}

interface DetailsWorkerType: CommonWorkerType<Detail, List<String>> {
    suspend fun fetchProductDetails(asins: List<String>, completion: LiveCompletionResponse<List<ProductDetail>>)
}