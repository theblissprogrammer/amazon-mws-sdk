package com.theblissprogrammer.amazon.sdk.data

import android.content.Context
import androidx.room.Room
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType

/**
 * Created by ahmed.saad on 2018-12-03.
 * Copyright © 2018. All rights reserved.
 */
class DataRoomStore(val context: Context, override val preferencesWorker: PreferencesWorkerType): DataStore {
    fun instance(name: String = this.name): AppDatabase?{
        return databases[name]
    }

    private var databases: MutableMap<String, AppDatabase> = mutableMapOf()

    init {
        this.configure()
    }

    override fun configure() {
        if (instance() == null)
            databases[this.name] = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, this.name
            ).build()
    }

    override fun delete(sellerID: String) {
        instance(generateName(sellerID))?.clearAllTables()
        databases.remove(generateName(sellerID))
    }
}