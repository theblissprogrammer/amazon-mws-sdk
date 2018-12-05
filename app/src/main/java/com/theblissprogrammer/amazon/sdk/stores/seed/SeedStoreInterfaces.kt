package com.theblissprogrammer.amazon.sdk.stores.seed

import com.theblissprogrammer.amazon.sdk.stores.seed.models.SeedPayload
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import java.util.*

/**
 * Created by ahmedsaad on 2018-08-23.
 * Copyright (c) 2018. All rights reserved.
 **/
interface SeedStore {
    fun fetchPayload(newerThan: Date? = null, completion: CompletionResponse<SeedPayload>)
}

interface SeedWorkerType: SeedStore