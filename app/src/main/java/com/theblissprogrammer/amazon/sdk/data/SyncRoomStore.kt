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
import com.theblissprogrammer.amazon.sdk.enums.marketplaceFromId
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.add
import com.theblissprogrammer.amazon.sdk.extensions.startOfDay
import com.theblissprogrammer.amazon.sdk.extensions.startOfMonth
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

    override fun remotePull(refresh: Boolean, completion: CompletionResponse<SeedPayload>?) {
        LogHelper.d(messages = *arrayOf("Pull from remote data source begins."))

        // Ensure configured with latest settings
        dataWorker.configure()

        seedOrders(refresh)
        seedInventories(refresh)

        completion?.invoke(success(null))
    }

    private fun seedOrders(refresh: Boolean) {
        val marketplaces = getSellerMarketplaces(preferencesWorker)

        if (refresh) {

            val cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"))
            cal.time = Date()

            (0..12).map {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - it)

                ReportModels.Request(
                        type = ReportType.OrderByUpdateDate,
                        date = cal.time.startOfMonth(),
                        marketplaces = marketplaces
                )
            }.forEach {
                reportsWorker.requestReport(it)
            }

        } else {
            val lastPulledAt = getSyncActivityLastPulledAt(
                    typeName = Order::class.java.simpleName,
                    suffix = marketplaces.joinToString())

            if (lastPulledAt != null && lastPulledAt.add(Calendar.MINUTE, 1).after(Date())) {
                return
            }

            val request = ReportModels.Request(
                    type = ReportType.OrderByUpdateDate,
                    date = lastPulledAt ?: Date().startOfMonth(),
                    marketplaces = marketplaces
            )

            reportsWorker.requestReport(request)
        }
    }

    private fun seedInventories(refresh: Boolean) {
        val marketplaces = getSellerMarketplaces(preferencesWorker)
        val lastPulledAt = getSyncActivityLastPulledAt(
                typeName = Inventory::class.java.simpleName,
                suffix = marketplaces.joinToString())

        if (lastPulledAt != null && lastPulledAt.add(Calendar.MINUTE, 30).after(Date())) {
            return
        }

        val request = ReportModels.Request(
                type = ReportType.FBAMYIInventory,
                marketplaces = marketplaces
        )

        reportsWorker.requestReport(request)
    }

    companion object {
        /// Determine region ID stored in user object
        fun getSellerMarketplaces(preferencesWorker: PreferencesWorkerType): List<MarketplaceType> {

            return listOf(marketplaceFromId(preferencesWorker.get(DefaultsKeys.marketplace)) ?: MarketplaceType.US)

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