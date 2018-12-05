package com.theblissprogrammer.amazon.sdk.data

import com.theblissprogrammer.amazon.sdk.stores.seed.models.SeedPayload
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse as AmznResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SyncWorker(private val store: SyncStore,
                 private val dataWorker: DataWorkerType): SyncWorkerType {

    override fun configure() = dataWorker.configure()

    override fun delete(sellerID: String) {
        dataWorker.delete(sellerID)
    }

    override fun clear() {
        pullTasks.clear()
        isPulling = false
    }

    companion object {
        // Handle simultaneous pull requests in a queue
        private const val pullQueue = "com.amzntracker.remotePull"
        private var pullTasks =  arrayListOf<AmznResult<SeedPayload>>()
        private var isPulling = false
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