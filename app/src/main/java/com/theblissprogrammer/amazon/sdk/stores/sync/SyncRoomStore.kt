package com.theblissprogrammer.amazon.sdk.stores.sync
import com.theblissprogrammer.amazon.sdk.stores.sync.models.Sync
import com.theblissprogrammer.amazon.sdk.stores.sync.models.SyncModels
import java.util.*

class SyncRoomStore(private val syncDAO: SyncDAO?): SyncCacheStore {

    /// Get sync activity for type.
    override fun getSyncActivityLastPulledAt(request: SyncModels.Request): Date? {
        return syncDAO?.fetchUpdatedAt(request.name, sellerKey = request.sellerKey)
    }

    /// Update or create last pulled at date for sync activity.
    override fun updateSyncActivity(request: SyncModels.UpdateRequest) {
        val sync = Sync(
                name = request.name,
                sellerKey = request.sellerKey,
                updatedAt = request.updatedAt
        )
        syncDAO?.update(sync)
    }
}