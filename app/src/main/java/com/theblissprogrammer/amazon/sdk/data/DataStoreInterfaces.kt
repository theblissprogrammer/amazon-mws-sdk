package com.theblissprogrammer.amazon.sdk.data

import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys.Companion.sellerID
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType

/**
 * Created by ahmedsaad on 2017-12-01.
 * Copyright © 2017. All rights reserved.
 */
interface DataStore {
    val preferencesWorker: PreferencesWorkerType

    /// Name for isolated database per user or use anonymously
    val name: String
        get() = generateName(preferencesWorker.get(sellerID) ?: "")

    /// Used for referencing databases not associated with the current user
    fun generateName(sellerID: String): String {
        return "user_$sellerID"
    }

    fun configure()
    fun delete(sellerID: String)
}

interface DataWorkerType {
    fun delete(sellerID: String)
    fun configure()
}