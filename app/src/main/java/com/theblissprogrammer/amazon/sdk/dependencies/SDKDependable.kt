package com.theblissprogrammer.amazon.sdk.dependencies

import android.app.Application
import android.content.Context
import com.theblissprogrammer.amazon.sdk.account.AuthenticationService
import com.theblissprogrammer.amazon.sdk.account.AuthenticationWorkerType
import com.theblissprogrammer.amazon.sdk.data.DataStore
import com.theblissprogrammer.amazon.sdk.data.DataWorkerType
import com.theblissprogrammer.amazon.sdk.stores.sync.SyncWorkerType
import com.theblissprogrammer.amazon.sdk.network.APISessionType
import com.theblissprogrammer.amazon.sdk.network.HTTPServiceType
import com.theblissprogrammer.amazon.sdk.network.SignedHelperType
import com.theblissprogrammer.amazon.sdk.preferences.ConstantsStore
import com.theblissprogrammer.amazon.sdk.preferences.ConstantsType
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesStore
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.security.SecurityStore
import com.theblissprogrammer.amazon.sdk.security.SecurityWorkerType
import com.theblissprogrammer.amazon.sdk.stores.details.DetailsCacheStore
import com.theblissprogrammer.amazon.sdk.stores.details.DetailsStore
import com.theblissprogrammer.amazon.sdk.stores.details.DetailsWorkerType
import com.theblissprogrammer.amazon.sdk.stores.inventory.InventoryCacheStore
import com.theblissprogrammer.amazon.sdk.stores.inventory.InventoryStore
import com.theblissprogrammer.amazon.sdk.stores.inventory.InventoryWorkerType
import com.theblissprogrammer.amazon.sdk.stores.orderItems.OrderItemsCacheStore
import com.theblissprogrammer.amazon.sdk.stores.orderItems.OrderItemsStore
import com.theblissprogrammer.amazon.sdk.stores.orderItems.OrderItemsWorkerType
import com.theblissprogrammer.amazon.sdk.stores.orders.OrdersCacheStore
import com.theblissprogrammer.amazon.sdk.stores.orders.OrdersStore
import com.theblissprogrammer.amazon.sdk.stores.orders.OrdersWorkerType
import com.theblissprogrammer.amazon.sdk.stores.products.ProductsCacheStore
import com.theblissprogrammer.amazon.sdk.stores.reports.ReportsCacheStore
import com.theblissprogrammer.amazon.sdk.stores.reports.ReportsStore
import com.theblissprogrammer.amazon.sdk.stores.reports.ReportsWorkerType
import com.theblissprogrammer.amazon.sdk.stores.seed.SeedStore
import com.theblissprogrammer.amazon.sdk.stores.seed.SeedWorkerType
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellersCacheStore
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellersStore
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellersWorkerType
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.SubscriptionsCacheStore
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.SubscriptionsStore
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.SubscriptionsWorkerType
import com.theblissprogrammer.amazon.sdk.stores.sync.SyncCacheStore


/**
 * Created by ahmedsaad on 2017-11-29.
 * Copyright © 2017. All rights reserved.
 */
interface SDKDependable {
    var application: Application

    val resolveContext: Context

    val resolveConstants: ConstantsType

    val resolveDataWorker: DataWorkerType
    val resolvePreferencesWorker: PreferencesWorkerType
    val resolveSecurityWorker: SecurityWorkerType

    val resolveConstantsStore: ConstantsStore
    val resolvePreferencesStore: PreferencesStore
    val resolveSecurityStore: SecurityStore
    val resolveDataStore: DataStore

    val resolveHTTPService: HTTPServiceType
    val resolveAPISessionService: APISessionType

    val resolveSignedHelper: SignedHelperType

    val resolveSyncWorker: SyncWorkerType
    val resolveSellersWorker: SellersWorkerType
    val resolveOrdersWorker: OrdersWorkerType
    val resolveOrderItemsWorker: OrderItemsWorkerType
    val resolveInventoryWorker: InventoryWorkerType
    val resolveReportsWorker: ReportsWorkerType
    val resolveSeedWorker: SeedWorkerType
    val resolveAuthenticationWorker: AuthenticationWorkerType
    val resolveDetailsWorker: DetailsWorkerType
    val resolveSubscriptionsWorker: SubscriptionsWorkerType

    val resolveSellersStore: SellersStore
    val resolveOrdersStore: OrdersStore
    val resolveOrderItemsStore: OrderItemsStore
    val resolveInventoryStore: InventoryStore
    val resolveReportsStore: ReportsStore
    val resolveSeedStore: SeedStore
    val resolveDetailsStore: DetailsStore
    val resolveSubscriptionsStore: SubscriptionsStore

    val resolveAuthenticationService: AuthenticationService

    val resolveSellersCacheStore: SellersCacheStore
    val resolveOrdersCacheStore: OrdersCacheStore
    val resolveOrderItemsCacheStore: OrderItemsCacheStore
    val resolveInventoryCacheStore: InventoryCacheStore
    val resolveDetailsCacheStore: DetailsCacheStore
    val resolveProductsCacheStore: ProductsCacheStore
    val resolveSubscriptionsCacheStore: SubscriptionsCacheStore
    val resolveSyncCacheStore: SyncCacheStore
    val resolveReportsCacheStore: ReportsCacheStore
}