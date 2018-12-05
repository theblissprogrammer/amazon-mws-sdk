package com.theblissprogrammer.amazon.sdk.dependencies

import android.app.Application
import android.content.Context
import com.theblissprogrammer.amazon.sdk.data.*
import com.theblissprogrammer.amazon.sdk.network.*
import com.theblissprogrammer.amazon.sdk.preferences.*
import com.theblissprogrammer.amazon.sdk.security.SecurityPreferenceStore
import com.theblissprogrammer.amazon.sdk.security.SecurityStore
import com.theblissprogrammer.amazon.sdk.security.SecurityWorker
import com.theblissprogrammer.amazon.sdk.security.SecurityWorkerType

/**
 * Created by ahmedsaad on 2017-11-30.
 * Copyright © 2017. All rights reserved.
 */
open class CoreDependency: CoreDependable {

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

    override val resolveSignedHelper: SignedHelperType by lazy {
        SignedRequestsHelper(
                secretKey = ""// resolveConstants().awsSecretKey // Override in store modules
        )
    }
}