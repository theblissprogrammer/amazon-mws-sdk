package com.theblissprogrammer.amazon.sdk.stores.sync

import com.theblissprogrammer.amazon.sdk.data.DataWorkerType
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKey
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.ReportType
import com.theblissprogrammer.amazon.sdk.extensions.*
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.stores.inventory.InventoryWorkerType
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.InventoryModels
import com.theblissprogrammer.amazon.sdk.stores.orders.models.Order
import com.theblissprogrammer.amazon.sdk.stores.reports.ReportsWorkerType
import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
import com.theblissprogrammer.amazon.sdk.stores.seed.models.SeedPayload
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellersWorkerType
import com.theblissprogrammer.amazon.sdk.stores.sync.models.SyncModels
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse as AmznResult
import java.lang.NullPointerException
import java.util.*

class SyncWorker(private val cacheStore: SyncCacheStore,
                 private val dataWorker: DataWorkerType,
                 private val preferencesWorker: PreferencesWorkerType,
                 private val reportsWorker: ReportsWorkerType,
                 private val inventoryWorker: InventoryWorkerType,
                 private val sellersWorker: SellersWorkerType): SyncWorkerType {

    override fun configure() {

        /*val syncList = ContentResolver.getPeriodicSyncs(
                syncAccount,
                AUTHORITY)

        ContentResolver.requestSync(syncAccount, AUTHORITY, Bundle.EMPTY)

        if (syncList.isNullOrEmpty()) {
            ContentResolver.addPeriodicSync(
                    syncAccount,
                    AUTHORITY,
                    Bundle.EMPTY,
                    SYNC_INTERVAL
            )
        }
*/
        dataWorker.configure()
    }

    /**
     * Create a new dummy account for the sync adapter
     */
    /*private fun createSyncAccount(): Account {
        val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        return Account(ACCOUNT, ACCOUNT_TYPE).also { newAccount ->
            accountManager.addAccountExplicitly(newAccount, null, null)
        }
    } */

    override fun delete(sellerID: String) {
        dataWorker.delete(sellerID)
    }

    override fun clear() {
        pullTasks.clear()
        isPulling = false

        /*ContentResolver.removePeriodicSync(
                syncAccount,
                AUTHORITY,
                Bundle.EMPTY
        )*/
    }

    companion object {
        // Handle simultaneous pull requests in a queue
        private const val pullQueue = "com.amzntracker.remotePull"
        private var pullTasks =  arrayListOf<AmznResult<SeedPayload>>()
        private var isPulling = false

        // Constants
        const val AUTHORITY = "com.theblissprogrammer.amazon.sdk.syncadapter"
        const val ACCOUNT_TYPE = "com.theblissprogrammer.amazon.sdk.account"
        const val ACCOUNT = "dummyaccount"

        // Sync interval constants
        const val SECONDS_PER_MINUTE = 60L
        const val SYNC_INTERVAL_IN_MINUTES = 15L // Min is 15 min
        const val SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE
    }

    override fun remotePull(refresh: Boolean, completion: AmznResult<SeedPayload>?) {
        /*coroutineOnUi {
            coroutineBackgroundAsync {
                seedOrders(refresh)
                seedInventories(refresh)
            }.await()
        }*/
    }

    private fun seedOrders(refresh: Boolean) {

        if (refresh) {

            val cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"))
            cal.time = Date()

            (0..12).map {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - it)

                ReportModels.Request(
                        type = ReportType.OrderByUpdateDate,
                        date = cal.time.startOfMonth(),
                        marketplaces = listOf(marketplace)
                )
            }.forEach {
                reportsWorker.requestReport(it)
            }

        } else {
            val lastPulledAt = cacheStore.getSyncActivityLastPulledAt(
                    request = SyncModels.Request(
                            name = Order::class.java.simpleName,
                            sellerKey = getSellerKey()
                    )
            )

            if (lastPulledAt != null && lastPulledAt.add(Calendar.MINUTE, 1).after(Date())) {
                return
            }

            val request = ReportModels.Request(
                    type = ReportType.OrderByUpdateDate,
                    date = lastPulledAt ?: Date().startOfMonth(),
                    marketplaces = listOf(marketplace)
            )

            reportsWorker.requestReport(request)
        }

        val updateRequest = SyncModels.UpdateRequest(
                name = Order::class.java.simpleName,
                sellerKey = getSellerKey(),
                updatedAt = Date()
        )

        cacheStore.updateSyncActivity(updateRequest)
    }

    private fun seedInventories(refresh: Boolean) {

        val lastPulledAt = cacheStore.getSyncActivityLastPulledAt(
                request = SyncModels.Request(
                        name = Inventory::class.java.simpleName,
                        sellerKey = getSellerKey()
                )
        )

        if (lastPulledAt != null && lastPulledAt.add(Calendar.MINUTE, 30).after(Date())) {
            return inventoryWorker.update(InventoryModels.Request(
                    marketplace = marketplace,
                    lastSync = lastPulledAt
            ))
        }

        val request = ReportModels.Request(
                type = ReportType.FBAMYIInventory,
                marketplaces = listOf(marketplace)
        )

        reportsWorker.requestReport(request)


        val updateRequest = SyncModels.UpdateRequest(
                name = Inventory::class.java.simpleName,
                sellerKey = getSellerKey(),
                updatedAt = Date()
        )

        cacheStore.updateSyncActivity(updateRequest)
    }

    private val marketplace: MarketplaceType by lazy {
        MarketplaceType.valueOf(preferencesWorker.get(DefaultsKeys.marketplace) ?: "US")
    }

    /// Determine region ID stored in user object
    private fun getSellerKey(): Long {
       return sellersWorker.fetchCurrentSeller().value?.id ?: throw NullPointerException()
    }
}