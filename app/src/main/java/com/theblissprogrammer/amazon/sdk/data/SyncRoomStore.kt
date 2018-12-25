package com.theblissprogrammer.amazon.sdk.data

import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.ReportType
import com.theblissprogrammer.amazon.sdk.extensions.coroutineCompletionOnUi
import com.theblissprogrammer.amazon.sdk.stores.inventories.models.InventoryType
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

    private fun seedOrders(completion: CompletionResponse<List<Order>>? = null) {
        val marketplaces = getSellerMarketplaces(preferencesWorker)
        val lastPulledAt = getSyncActivityLastPulledAt(
                typeName = Order::class.java.simpleName,
                suffix = marketplaces?.joinToString() ?: "US")

        val cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"))

        val request = ReportModels.Request(
                type = ReportType.OrderByUpdateDate,
                date = lastPulledAt ?: cal.time.startOfDay().add(Calendar.DATE, -30),
                marketplaces = marketplaces ?: listOf(MarketplaceType.US)
        )

        coroutineCompletionOnUi(completion) {
            val timeStamp = cal.time

            fun saveOrders (response: Result<List<Order>>) {
                val value = response.value
                if (!response.isSuccess || value == null) {
                    LogHelper.e(messages = *arrayOf("Failed to get report orders: ${response.error}"))
                    return
                }

                // Ensure there is data before proceeding
                if (value.isEmpty()) {
                    LogHelper.d(messages = *arrayOf("Data seed for orders not modified for marketplace " +
                            "${marketplaces?.joinToString(", ")}."))
                    return
                }

                coroutineCompletionOnUi(completion) {
                    // Write source data to local storage
                    /*realmCoroutine<List<Order>> { realm ->
                        val orders = value.toRealmList()

                        realm.executeTransaction { _ ->
                            realm.insertOrUpdate(orders)
                        }

                        success(value)
                    }.await()*/
                }
            }

            val response = reportsWorker
                    .fetchOrderReport(request = request, completion = { saveOrders(it) }).await()

            val value = response.value
            if (!response.isSuccess || value == null) {
                val error = response.error ?: DataError.UnknownReason(null)
                LogHelper.e(messages = *arrayOf("Failed to get report orders: ${response.error}"))
                completion?.invoke(failure(error))
                return@coroutineCompletionOnUi
            }

            // Ensure there is data before proceeding
            if (value.isEmpty()) {
                LogHelper.d(messages = *arrayOf("Data seed for orders not modified for marketplace " +
                        "${marketplaces?.joinToString(", ")}."))
                completion?.invoke(success(value))
                return@coroutineCompletionOnUi
            }

            coroutineCompletionOnUi(completion) {
               /* // Write source data to local storage
                val cached = realmCoroutine<List<Order>> { realm ->
                    val orders = value.toRealmList()

                    realm.executeTransaction { _ ->
                        realm.insertOrUpdate(orders)
                    }

                    // Persist sync date for next use if applicable
                    SyncRoomStore.updateSyncActivity(typeName = Order::class.java.simpleName,
                            lastPulledAt = timeStamp,
                            suffix = marketplaces?.joinToString() ?: "US")
                    LogHelper.d(messages = *arrayOf("Data seed for order report complete for marketplaces " +
                            "${marketplaces?.joinToString(", ")}."))

                    success(value)
                }.await()

                completion?.invoke(cached)*/
            }
        }

    }

    private fun seedInventories(completion: CompletionResponse<List<InventoryType>>? = null) {
        val marketplaces = getSellerMarketplaces(preferencesWorker)
        val lastPulledAt = getSyncActivityLastPulledAt(
                typeName = InventoryType::class.java.simpleName,
                suffix = marketplaces?.joinToString() ?: "US")

        if (lastPulledAt != null && lastPulledAt.add(Calendar.MINUTE, 30).after(Date())) {
            completion?.invoke(failure(DataError.NonExistent))
            return
        }

        val cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"))

        val request = ReportModels.Request(
                type = ReportType.InventoryAFN,
                marketplaces = marketplaces ?: listOf(MarketplaceType.US)
        )

        coroutineCompletionOnUi(completion) {
            val timeStamp = cal.time

            fun saveInventories (response: Result<List<InventoryType>>) {
                val value = response.value
                if (!response.isSuccess || value == null) {
                    LogHelper.e(messages = *arrayOf("Failed to get report inventories: ${response.error}"))
                    return
                }

                // Ensure there is data before proceeding
                if (value.isEmpty()) {
                    LogHelper.d(messages = *arrayOf("Data seed for inventories not modified for marketplace " +
                            "${marketplaces?.joinToString(", ")}."))
                    return
                }

                coroutineCompletionOnUi(completion) {
                    // Write source data to local storage
                    /*realmCoroutine<List<InventoryType>> { realm ->
                        val inventories = value.toRealmList()

                        realm.executeTransaction { _ ->
                            realm.insertOrUpdate(inventories)
                        }

                        success(value)
                    }.await()*/
                }
            }

            val response = reportsWorker
                    .fetchInventoryReport(request = request, completion = { saveInventories(it) }).await()

            // Handle errors if applicable
            val value = response.value
            if (!response.isSuccess || value == null) {
                val error = response.error ?: DataError.UnknownReason(null)
                LogHelper.e(messages = *arrayOf("Failed to get report inventories: ${response.error}"))
                completion?.invoke(failure(error))
                return@coroutineCompletionOnUi
            }

            // Ensure there is data before proceeding
            if (value.isEmpty()) {
                LogHelper.d(messages = *arrayOf("Data seed for inventories not modified for marketplace " +
                        "${marketplaces?.joinToString(", ")}."))
                completion?.invoke(success(value))
                return@coroutineCompletionOnUi
            }

            // Write source data to local storage
            /*val cached = realmCoroutine<List<InventoryType>> { realm ->
                val inventories = value.toRealmList()

                realm.executeTransaction { _ ->
                    realm.insertOrUpdate(inventories)
                }

                // Persist sync date for next use if applicable
                SyncRoomStore.updateSyncActivity(typeName = InventoryType::class.java.simpleName,
                        lastPulledAt = timeStamp,
                        suffix = marketplaces?.joinToString() ?: "US")
                LogHelper.d(messages = *arrayOf("Data seed for inventory report complete for marketplaces " +
                        "${marketplaces?.joinToString( ", ")}."))

                success(value)
            }.await()

            completion?.invoke(cached)*/
        }

    }

    private fun seedProducts(completion: CompletionResponse<List<Product>>? = null) {
        val marketplaces = getSellerMarketplaces(preferencesWorker)
        val lastPulledAt = getSyncActivityLastPulledAt(
                typeName = Product::class.java.simpleName,
                suffix = marketplaces?.joinToString() ?: "US")

        if (lastPulledAt != null && lastPulledAt.add(Calendar.MINUTE, 30).after(Date())) {
            completion?.invoke(failure(DataError.NonExistent))
            return
        }

        val cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"))

        val request = ReportModels.Request(
                type = ReportType.AllListings,
                marketplaces = marketplaces ?: listOf(MarketplaceType.US)
        )

        coroutineCompletionOnUi(completion) {
            val timeStamp = cal.time

            fun saveProducts(response: Result<List<Product>>) {
                val value = response.value
                if (!response.isSuccess || value == null) {
                    LogHelper.e(messages = *arrayOf("Failed to get report products: ${response.error}"))
                    return
                }

                // Ensure there is data before proceeding
                if (value.isEmpty()) {
                    LogHelper.d(messages = *arrayOf("Data seed for products not modified for marketplace " +
                            "${marketplaces?.joinToString(", ")}."))
                    return
                }

                coroutineCompletionOnUi(completion) {
                    // Write source data to local storage
                    /*realmCoroutine<List<Product>> { realm ->
                        val products = value.toRealmList()

                        realm.executeTransaction { _ ->
                            realm.insertOrUpdate(products)
                        }

                        success(value)
                    }.await()*/
                }
            }

            val response = reportsWorker
                    .fetchProductReport(request = request, completion = { saveProducts(it) }).await()

            // Handle errors if applicable
            val value = response.value
            if (!response.isSuccess || value == null) {
                val error = response.error ?: DataError.UnknownReason(null)
                LogHelper.e(messages = *arrayOf("Failed to get report products: ${response.error}"))
                completion?.invoke(failure(error))
                return@coroutineCompletionOnUi
            }

            // Ensure there is data before proceeding
            if (value.isEmpty()) {
                LogHelper.d(messages = *arrayOf("Data seed for products not modified for marketplace " +
                        "${marketplaces?.joinToString(", ")}."))
                completion?.invoke(success(value))
                return@coroutineCompletionOnUi
            }

            // Write source data to local storage
            /*val cached = realmCoroutine<List<Product>> { realm ->
                val products = value.toRealmList()

                realm.executeTransaction { _ ->
                    realm.insertOrUpdate(products)
                }

                // Persist sync date for next use if applicable
                SyncRoomStore.updateSyncActivity(typeName = Product::class.java.simpleName,
                        lastPulledAt = timeStamp,
                        suffix = marketplaces?.joinToString() ?: "US")
                LogHelper.d(messages = *arrayOf("Data seed for product report complete for marketplaces " +
                        "${marketplaces?.joinToString(", ")}."))

                success(value)
            }.await()

            completion?.invoke(cached)*/
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