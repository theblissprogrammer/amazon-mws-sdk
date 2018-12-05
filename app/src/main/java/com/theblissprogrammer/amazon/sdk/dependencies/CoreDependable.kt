package com.theblissprogrammer.amazon.sdk.dependencies

import android.app.Application
import android.content.Context
import com.theblissprogrammer.amazon.sdk.network.HTTPServiceType
import com.theblissprogrammer.amazon.sdk.data.DataStore
import com.theblissprogrammer.amazon.sdk.data.DataWorkerType
import com.theblissprogrammer.amazon.sdk.network.APISessionType
import com.theblissprogrammer.amazon.sdk.network.SignedHelperType
import com.theblissprogrammer.amazon.sdk.preferences.ConstantsStore
import com.theblissprogrammer.amazon.sdk.preferences.ConstantsType
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesStore
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.security.SecurityStore
import com.theblissprogrammer.amazon.sdk.security.SecurityWorkerType

/**
 * Created by ahmedsaad on 2017-11-29.
 * Copyright © 2017. All rights reserved.
 */
interface CoreDependable {
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
}