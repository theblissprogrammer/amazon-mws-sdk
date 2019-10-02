package com.theblissprogrammer.amazon.sdk.data

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.Context
import android.os.Bundle
import androidx.core.content.ContextCompat.getSystemService
import com.theblissprogrammer.amazon.sdk.stores.seed.models.SeedPayload
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse as AmznResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SyncWorker(private val store: SyncStore,
                 private val dataWorker: DataWorkerType,
                 private val context: Context): SyncWorkerType {

    private val syncAccount by lazy {
        createSyncAccount()
    }

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
    private fun createSyncAccount(): Account {
        val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        return Account(ACCOUNT, ACCOUNT_TYPE).also { newAccount ->
            accountManager.addAccountExplicitly(newAccount, null, null)
        }
    }

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

    override fun remotePull(completion: AmznResult<SeedPayload>?) {
        synchronized(pullQueue) {
            if (completion != null) {
                pullTasks.add(completion)
            }

            if (isPulling) {
                return@synchronized
            }
            isPulling = true

            store.remotePull { result ->
                synchronized(pullQueue) {
                    val tasks = ArrayList(pullTasks)
                    pullTasks.clear()
                    isPulling = false

                    GlobalScope.launch(Dispatchers.Main) {
                        tasks.forEach {
                            it(result)
                        }
                    }
                }
            }
        }
    }
}