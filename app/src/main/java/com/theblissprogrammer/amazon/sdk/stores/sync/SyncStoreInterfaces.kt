package com.theblissprogrammer.amazon.sdk.stores.sync

import com.theblissprogrammer.amazon.sdk.stores.seed.models.SeedPayload
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.stores.sync.models.SyncModels
import java.util.*


interface SyncCacheStore {
    fun getSyncActivityLastPulledAt(request: SyncModels.Request): Date?
    fun updateSyncActivity(request: SyncModels.UpdateRequest)
}

interface SyncWorkerType {
    fun remotePull(refresh: Boolean = false, completion: CompletionResponse<SeedPayload>? = null)
    fun delete(sellerID: String)
    fun clear()
    fun configure()
}