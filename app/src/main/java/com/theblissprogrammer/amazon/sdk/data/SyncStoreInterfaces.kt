package com.theblissprogrammer.amazon.sdk.data

import com.theblissprogrammer.amazon.sdk.stores.seed.models.SeedPayload
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse


interface SyncStore {
    fun remotePull(completion: CompletionResponse<SeedPayload>? = null)
}

interface SyncWorkerType: SyncStore {
    fun delete(sellerID: String)
    fun clear()
    fun configure()
}