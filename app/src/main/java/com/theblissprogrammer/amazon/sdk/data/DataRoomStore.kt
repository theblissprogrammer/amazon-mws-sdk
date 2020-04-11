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
        return databases
    }

    private val databases: AppDatabase by lazy {
        Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "SalesTracker"
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .fallbackToDestructiveMigration()
                .build()
    }

    init {
        this.configure()
    }

    override fun configure() {

    }

    override fun delete(sellerID: String) {
        instance(generateName(sellerID))?.clearAllTables()
        //databases.remove(generateName(sellerID))
    }
}