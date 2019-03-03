package com.theblissprogrammer.amazon.sdk.data

import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.ReportType
import com.theblissprogrammer.amazon.sdk.extensions.coroutineCompletionOnUi
import com.theblissprogrammer.amazon.sdk.stores.orders.models.Order
import com.theblissprogrammer.amazon.sdk.stores.reports.ReportsWorkerType
import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
import com.theblissprogrammer.amazon.sdk.stores.seed.SeedWorkerType
import com.theblissprogrammer.amazon.sdk.stores.seed.models.SeedPayload
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.failure
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.success
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.add
import com.theblissprogrammer.amazon.sdk.extensions.startOfDay
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product
import java.util.*

class SyncRoomStore(val preferencesWorker: PreferencesWorkerType,
                    val seedWorker: SeedWorkerType,
                    val dataWorker: DataWorkerType,
                    val reportsWorker: ReportsWorkerType): SyncStore {

    private val isAuthenticated: Boolean
        get() {
            return !preferencesWorker.get(DefaultsKeys.sellerID).isNullOrEmpty()
        }

    override fun remotePull(completion: CompletionResponse<SeedPayload>?) {
        LogHelper.d(messages = *arrayOf("Pull from remote data source begins."))

        // Ensure configured with latest settings
        dataWorker.configure()

        seedPayload {
            if (it.value == null || !it.isSuccess) {
                completion?.invoke(failure(it.error ?: DataError.UnknownReason(null)))
                return@seedPayload
            }

            completion?.invoke(success(it.value))
        }
    }

    private fun seedPayload(completion: CompletionResponse<SeedPayload>?) {
        val marketplaces = getSellerMarketplaces(preferencesWorker)
        val lastPulledAt = getSyncActivityLastPulledAt(
                typeName = SeedPayload::class.java.simpleName,
                suffix = marketplaces?.joinToString() ?: "US")

        if (lastPulledAt != null && lastPulledAt.add(Calendar.MINUTE, 30).after(Date())) {
            completion?.invoke(failure(DataError.NonExistent))
            return
        }

        coroutineCompletionOnUi(completion) {
            val cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"))
            val timeStamp = cal.time

            seedWorker.fetchPayload(newerThan = lastPulledAt ?: timeStamp.startOfDay().add(Calendar.DATE, -30)) {
                // Handle errors if applicable
                val value = it.value
                if (!it.isSuccess || value == null) {
                    val error = it.error ?: DataError.UnknownReason(null)
                    LogHelper.e(messages = *arrayOf("Failed to get seed payload: $error"))
                    completion?.invoke(failure(error))
                    return@fetchPayload
                }

                // Ensure there is data before proceeding
                if (value.isEmpty) {
                    LogHelper.d(messages = *arrayOf("Data seed for payload not modified for marketplaces ${marketplaces?.joinToString()}."))
                    completion?.invoke(success(value))
                    return@fetchPayload
                }

                // Write source data to local storage
                /*callRealmInBackgroundWithCompletionOnUi(completion = completion) { realm, tcs ->
                    // Transform data
                    val orders = value.orders.toRealmList()
                    val inventories = value.inventories.toRealmList()
                    val products = value.products.toRealmList()
                    val fbaFees = value.fbaFees.toRealmList()

                    realm.executeTransaction {
                        realm.insertOrUpdate(orders)
                        realm.insertOrUpdate(inventories)
                        realm.insertOrUpdate(products)
                        realm.insertOrUpdate(fbaFees)
                    }

                    // Persist sync date for next use if applicable
                    SyncRoomStore.updateSyncActivity(typeName = SeedPayload::class.java.simpleName,
                            lastPulledAt = timeStamp,
                            suffix = marketplaces?.joinToString() ?: "US")
                    LogHelper.d(messages = *arrayOf("Data seed for secondary payload complete."))


                    tcs.setResult(value)
                }*/
            }
        }
    }

    companion object {
        /// Determine region ID stored in user object
        fun getSellerMarketplaces(preferencesWorker: PreferencesWorkerType): List<MarketplaceType>? {

            /*val realm = Realm.getDefaultInstance()
            return try {
                val sellerID = preferencesWorker.get(sellerID)
                if (sellerID != null) {
                    val query = realm.where(SellerRealmObject::class.java)
                            .equalTo("id", sellerID)

                    return query.findAll()?.map { it.marketplace }
                }
                null
            } catch (error: Exception) {
                LogHelper.e(messages = *arrayOf("Could not initialize database: ${error.localizedMessage}"))
                null
            } finally {
                realm.close()
            }*/
            return null
        }

        /// Get sync activity for type.
        fun getSyncActivityLastPulledAt(typeName: String, suffix: String = ""): Date? {
            /*val realm: Realm = Realm.getDefaultInstance()
            return try {
                // Build the query looking at all users:
                val query = realm.where(SyncActivity::class.java)

                // Add query conditions:
                query.equalTo("type", "$typeName $suffix")

                query.findFirst()?.lastPulledAt
            } catch (error: Exception) {
                LogHelper.e(messages = *arrayOf("Could not initialize database: ${error.localizedMessage}"))
                null
            } finally {
                realm.close()
            }*/
            return null
        }

        /// Update or create last pulled at date for sync activity.
        private fun updateSyncActivity(typeName: String, lastPulledAt: Date, suffix: String = "") {
            /*val realm = Realm.getDefaultInstance()

            try {
                val syncActivity = SyncActivity(
                        type = "$typeName $suffix",
                        lastPulledAt = lastPulledAt)

                realm.executeTransaction {
                    realm.insertOrUpdate(syncActivity)
                }

            } catch (error: Exception)  {
                LogHelper.e(messages = *arrayOf("Could not write sync activity to Realm for $typeName: ${error.localizedMessage}"))
            } finally {
                realm.close()
            }*/
        }
    }
}