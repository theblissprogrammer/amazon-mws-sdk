package com.theblissprogrammer.amazon.sdk.stores.products

import com.theblissprogrammer.amazon.sdk.stores.common.CommonCacheStore
import com.theblissprogrammer.amazon.sdk.stores.common.CommonStore
import com.theblissprogrammer.amazon.sdk.stores.common.CommonWorkerType
import com.theblissprogrammer.amazon.sdk.stores.price.models.MyPriceForSKU
import com.theblissprogrammer.amazon.sdk.stores.price.models.Price
import com.theblissprogrammer.amazon.sdk.stores.products.models.PriceModels

/**
 * Created by ahmedsaad on 2018-08-10.
 * Copyright (c) 2018. All rights reserved.
 **/
interface PriceStore: CommonStore<MyPriceForSKU, PriceModels.Request>

interface PriceCacheStore: CommonCacheStore<Price, PriceModels.Request>

interface PriceWorkerType: CommonWorkerType<Price, PriceModels.Request>