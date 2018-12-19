package com.theblissprogrammer.amazon.sdk.dependencies

import android.app.Application
import android.content.Context
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellersNetworkStore
import com.theblissprogrammer.amazon.sdk.account.AuthenticationService
import com.theblissprogrammer.amazon.sdk.account.AuthenticationWorker
import com.theblissprogrammer.amazon.sdk.account.AuthenticationWorkerType
import com.theblissprogrammer.amazon.sdk.account.AuthenticationNetworkService
import com.theblissprogrammer.amazon.sdk.data.*
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.RegionType
import com.theblissprogrammer.amazon.sdk.stores.inventories.*
import com.theblissprogrammer.amazon.sdk.stores.orders.*
import com.theblissprogrammer.amazon.sdk.stores.reports.ReportsNetworkStore
import com.theblissprogrammer.amazon.sdk.stores.reports.ReportsStore
import com.theblissprogrammer.amazon.sdk.stores.reports.ReportsWorker
import com.theblissprogrammer.amazon.sdk.stores.reports.ReportsWorkerType
import com.theblissprogrammer.amazon.sdk.stores.seed.SeedNetworkStore
import com.theblissprogrammer.amazon.sdk.stores.seed.SeedStore
import com.theblissprogrammer.amazon.sdk.stores.seed.SeedWorker
import com.theblissprogrammer.amazon.sdk.stores.seed.SeedWorkerType
import com.theblissprogrammer.amazon.sdk.stores.sellers.*
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys
import com.theblissprogrammer.amazon.sdk.network.*
import com.theblissprogrammer.amazon.sdk.preferences.*
import com.theblissprogrammer.amazon.sdk.security.SecurityPreferenceStore
import com.theblissprogrammer.amazon.sdk.security.SecurityStore
import com.theblissprogrammer.amazon.sdk.security.SecurityWorker
import com.theblissprogrammer.amazon.sdk.security.SecurityWorkerType

open class SDKDependency: SDKDependable {
    override lateinit var application: Application

    override val resolveContext: Context by lazy {
        application.applicationContext
    }

    override val resolveConstants: ConstantsType by lazy {
        Constants(
            store = resolveConstantsStore
        )
    }

    // Workers

    override val resolvePreferencesWorker: PreferencesWorkerType by lazy {
        PreferencesWorker(store = resolvePreferencesStore)
    }

    override val resolveSecurityWorker: SecurityWorkerType by lazy {
        SecurityWorker(
            context = resolveContext,
            store = resolveSecurityStore
        )
    }

    override val resolveDataWorker: DataWorkerType by lazy {
        DataWorker(store = resolveDataStore)
    }

    // Stores

    override val resolveConstantsStore: ConstantsStore by lazy {
        ConstantsResourceStore(
            context = resolveContext
        )
    }

    override val resolveDataStore: DataStore by lazy {
        DataRoomStore(
            context = resolveContext,
            preferencesWorker = resolvePreferencesWorker
        )
    }

    override val resolvePreferencesStore: PreferencesStore by lazy {
        PreferencesDefaultsStore(context = resolveContext)
    }

    override val resolveSecurityStore: SecurityStore by lazy {
        SecurityPreferenceStore(context = resolveContext)
    }

    // Services

    override val resolveHTTPService: HTTPServiceType by lazy {
        HTTPService()
    }

    override val resolveAPISessionService: APISessionType by lazy {
        APISession(
            context = resolveContext,
            constants = resolveConstants,
            securityWorker = resolveSecurityWorker,
            preferencesWorker = resolvePreferencesWorker,
            signedHelper = resolveSignedHelper
        )
    }

    override val resolveSyncWorker: SyncWorkerType  by lazy {
         SyncWorker(
                store = resolveSyncStore,
                dataWorker = resolveDataWorker
        )
    }

    override val resolveSellersWorker: SellersWorkerType  by lazy {
         SellersWorker(
                store = resolveSellersStore,
                cacheStore = resolveSellersCacheStore,
                preferencesWorker = resolvePreferencesWorker
        )
    }

    override val resolveOrdersWorker: OrdersWorkerType  by lazy {
         OrdersWorker(
                store = resolveOrdersStore,
                cacheStore = resolveOrdersCacheStore,
                preferencesWorker = resolvePreferencesWorker
        )
    }

    override val resolveInventoriesWorker: InventoriesWorkerType  by lazy {
         InventoriesWorker(
                store = resolveInventoriesStore,
                cacheStore = resolveInventoriesCacheStore
        )
    }

    override val resolveReportsWorker: ReportsWorkerType  by lazy {
         ReportsWorker(
                store = resolveReportsStore
        )
    }

    override val resolveSeedWorker: SeedWorkerType  by lazy {
         SeedWorker(
                store = resolveSeedStore
        )
    }

    override val resolveAuthenticationWorker: AuthenticationWorkerType  by lazy {
         AuthenticationWorker(
                service = resolveAuthenticationService,
                preferencesWorker = resolvePreferencesWorker,
                syncWorker = resolveSyncWorker,
                securityWorker = resolveSecurityWorker,
                context = resolveContext,
                sellersCacheStore = resolveSellersCacheStore
        )
    }

    override val resolveSyncStore: SyncStore  by lazy {
         SyncRoomStore(
                preferencesWorker = resolvePreferencesWorker,
                dataWorker = resolveDataWorker,
                reportsWorker = resolveReportsWorker,
                seedWorker = resolveSeedWorker
        )
    }

    override val resolveSellersStore: SellersStore  by lazy {
         SellersNetworkStore(
                httpService = resolveHTTPService
        )
    }

    override val resolveOrdersStore: OrdersStore  by lazy {
         OrdersNetworkStore(
                apiSession = resolveAPISessionService
        )
    }

    override val resolveInventoriesStore: InventoriesStore  by lazy {
         InventoriesNetworkStore(
                apiSession = resolveAPISessionService
        )
    }

    override val resolveReportsStore: ReportsStore  by lazy {
         ReportsNetworkStore(
                apiSession = resolveAPISessionService
        )
    }

    override val resolveSeedStore: SeedStore  by lazy {
         SeedNetworkStore(
                reportsStore = resolveReportsStore as ReportsNetworkStore,
                preferencesWorker = resolvePreferencesWorker
        )
    }

    override val resolveAuthenticationService: AuthenticationService  by lazy {
         AuthenticationNetworkService(
                apiSession = resolveAPISessionService,
                preferencesWorker = resolvePreferencesWorker
        )
    }

    override val resolveSellersCacheStore: SellersCacheStore  by lazy {
        SellersRoomStore(
             sellerDao = (resolveDataStore as? DataRoomStore)?.instance()?.sellerDao()
        )
    }

    override val resolveOrdersCacheStore: OrdersCacheStore  by lazy {
        OrdersRoomStore(
            orderDao = (resolveDataStore as? DataRoomStore)?.instance()?.orderDao()
        )
    }

    override val resolveInventoriesCacheStore: InventoriesCacheStore?  by lazy {
         null
    }

    private val region: RegionType by lazy {
        MarketplaceType.valueOf(resolvePreferencesWorker.get(DefaultsKeys.marketplace) ?: "US").region
    }

    override val resolveSignedHelper: SignedHelperType  by lazy {
         SignedRequestsHelper(
                secretKey = when (region) {
                    RegionType.EU -> resolveConstants.euAwsSecretKey
                    else -> resolveConstants.awsSecretKey
                }
        )
    }

}