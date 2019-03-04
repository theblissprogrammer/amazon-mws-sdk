package com.theblissprogrammer.amazon.sdk.stores.products

import com.theblissprogrammer.amazon.sdk.stores.common.CommonCacheStore
import com.theblissprogrammer.amazon.sdk.stores.common.CommonStore
import com.theblissprogrammer.amazon.sdk.stores.common.CommonWorkerType
import com.theblissprogrammer.amazon.sdk.stores.products.models.PriceModels
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product

/**
 * Created by ahmedsaad on 2018-08-10.
 * Copyright (c) 2018. All rights reserved.
 **/
interface ProductsStore: CommonStore<Product, String>

interface ProductsCacheStore: CommonCacheStore<Product, List<String>>

interface ProductsWorkerType: CommonWorkerType<Product, List<String>>