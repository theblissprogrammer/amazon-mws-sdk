package com.theblissprogrammer.amazon.sdk.dependencies

import com.theblissprogrammer.amazon.sdk.account.AuthenticationService
import com.theblissprogrammer.amazon.sdk.account.AuthenticationWorkerType
import com.theblissprogrammer.amazon.sdk.data.SyncStore
import com.theblissprogrammer.amazon.sdk.data.SyncWorkerType
import com.theblissprogrammer.amazon.sdk.stores.inventories.InventoriesCacheStore
import com.theblissprogrammer.amazon.sdk.stores.inventories.InventoriesStore
import com.theblissprogrammer.amazon.sdk.stores.inventories.InventoriesWorkerType
import com.theblissprogrammer.amazon.sdk.stores.orders.OrdersCacheStore
import com.theblissprogrammer.amazon.sdk.stores.orders.OrdersStore
import com.theblissprogrammer.amazon.sdk.stores.orders.OrdersWorkerType
import com.theblissprogrammer.amazon.sdk.stores.reports.ReportsStore
import com.theblissprogrammer.amazon.sdk.stores.reports.ReportsWorkerType
import com.theblissprogrammer.amazon.sdk.stores.seed.SeedStore
import com.theblissprogrammer.amazon.sdk.stores.seed.SeedWorkerType
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellersCacheStore
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellersStore
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellersWorkerType


/**
 * Created by ahmedsaad on 2017-11-29.
 * Copyright © 2017. All rights reserved.
 */
interface SDKDependable: CoreDependable {

    val resolveSyncWorker: SyncWorkerType
    val resolveSellersWorker: SellersWorkerType
    val resolveOrdersWorker: OrdersWorkerType
    val resolveInventoriesWorker: InventoriesWorkerType
    val resolveReportsWorker: ReportsWorkerType
    val resolveSeedWorker: SeedWorkerType
    val resolveAuthenticationWorker: AuthenticationWorkerType

    val resolveSyncStore: SyncStore
    val resolveSellersStore: SellersStore
    val resolveOrdersStore: OrdersStore
    val resolveInventoriesStore: InventoriesStore
    val resolveReportsStore: ReportsStore
    val resolveSeedStore: SeedStore

    val resolveAuthenticationService: AuthenticationService

    val resolveSellersCacheStore: SellersCacheStore
    val resolveOrdersCacheStore: OrdersCacheStore?
    val resolveInventoriesCacheStore: InventoriesCacheStore?
}