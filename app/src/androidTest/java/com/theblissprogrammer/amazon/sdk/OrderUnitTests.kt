package com.theblissprogrammer.amazon.sdk

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.theblissprogrammer.amazon.sdk.access.MwsSdk
import com.theblissprogrammer.amazon.sdk.data.AppDatabase
import com.theblissprogrammer.amazon.sdk.data.MIGRATION_1_2
import com.theblissprogrammer.amazon.sdk.dependencies.HasDependencies
import com.theblissprogrammer.amazon.sdk.stores.orders.OrderDAO
import com.theblissprogrammer.amazon.sdk.stores.orders.OrdersCacheStore
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Created by ahmed.saad on 2018-12-07.
 * Copyright © 2018. All rights reserved.
 */
@RunWith(AndroidJUnit4::class)
class OrderUnitTests: HasDependencies {
    private lateinit var orderDao: OrderDAO
    private lateinit var db: AppDatabase

    private val ordersWorker by lazy {
        dependencies.resolveOrdersWorker
    }

    private val ordersRoomStore: OrdersCacheStore by lazy {
        dependencies.resolveOrdersCacheStore
    }

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun configureSdk() {
        MwsSdk.configure(
            application = InstrumentationRegistry.getTargetContext().applicationContext as Application,
            dependencies = MockSDKDependency()
        )
    }

    @Before
    fun createDb() {
        val context: Context = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).addMigrations(MIGRATION_1_2).build()
        orderDao = db.orderDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}
