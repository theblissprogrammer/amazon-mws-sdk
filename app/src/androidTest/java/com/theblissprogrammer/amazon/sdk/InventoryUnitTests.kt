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
import com.theblissprogrammer.amazon.sdk.data.MIGRATION_2_3
import com.theblissprogrammer.amazon.sdk.data.MIGRATION_3_4
import com.theblissprogrammer.amazon.sdk.dependencies.HasDependencies
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.OrderStatus
import com.theblissprogrammer.amazon.sdk.extensions.add
import com.theblissprogrammer.amazon.sdk.extensions.endOfDay
import com.theblissprogrammer.amazon.sdk.extensions.startOfDay
import com.theblissprogrammer.amazon.sdk.stores.inventory.InventoryCacheStore
import com.theblissprogrammer.amazon.sdk.stores.inventory.InventoryDAO
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.InventoryModels
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Quantity
import com.theblissprogrammer.amazon.sdk.stores.orderItems.OrderItemDAO
import com.theblissprogrammer.amazon.sdk.stores.orderItems.OrderItemsCacheStore
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.OrderItem
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.OrderItemModels
import com.theblissprogrammer.amazon.sdk.stores.orders.OrderDAO
import com.theblissprogrammer.amazon.sdk.stores.orders.OrdersCacheStore
import com.theblissprogrammer.amazon.sdk.stores.orders.models.ListOrder
import com.theblissprogrammer.amazon.sdk.stores.orders.models.Order
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderModels
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellerDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

/**
 * Created by ahmed.saad on 2018-12-07.
 * Copyright © 2018. All rights reserved.
 */
@RunWith(AndroidJUnit4::class)
class InventoryUnitTests: HasDependencies {
    private lateinit var inventoryDao: InventoryDAO
    private lateinit var db: AppDatabase

    private val inventoryWorker by lazy {
        dependencies.resolveInventoryWorker
    }

    private val inventoryRoomStore: InventoryCacheStore by lazy {
        dependencies.resolveInventoryCacheStore
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
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .fallbackToDestructiveMigration().build()
        inventoryDao = db.inventoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun saving_and_fetching_db() {
        val sku = "1234"
        val saveItem = Inventory(sku = sku, quantity = Quantity(total = 5, instock = 5))

        runBlocking {
            val inventory = withContext(Dispatchers.IO) {
                inventoryDao.insert(saveItem)
                inventoryDao.fetchSync(sku)
            }

            Assert.assertNotNull("The number of inventory must equal to number added.", inventory)
            Assert.assertEquals("The number of inventory sku must equal to the one inserted.", sku, inventory.sku)
            Assert.assertEquals("The number of inventory quantity total must equal to the one inserted.", 5, inventory.quantity.total)
            Assert.assertEquals("The number of inventory quantity instock must equal to the one inserted.", 5, inventory.quantity.instock)
        }
    }

    @Test
    fun fetch_inventory() {
        runBlocking {
            inventoryWorker.fetch(request = InventoryModels.Request(
                lastSync = Date().add(Calendar.DATE, -30).startOfDay()
            )) {
                Assert.assertTrue(
                    "An error occurred when there should not be: ${it.error?.localizedMessage ?: it.error}",
                    it.isSuccess
                )
                Assert.assertNull("Inventory should return a null error.", it.error)

                val inventory = getValue(it.value)
                Assert.assertNotNull("Worker should return valid order object.", inventory)
            }
        }
    }

    @Test
    fun fetch_inventory_by_sku() {
        runBlocking {
            val sku = "114-8859035-8931444"
            inventoryWorker.fetch(request = InventoryModels.Request(
                skus = listOf(sku)
            )) {
                Assert.assertTrue(
                    "An error occurred when there should not be: ${it.error?.localizedMessage ?: it.error}",
                    it.isSuccess
                )
                Assert.assertNull("Inventory should return a null error.", it.error)

                val inventory = getValue(it.value)
                Assert.assertNotNull("Worker should return valid order object.", inventory)
                Assert.assertEquals("The inventory sku should match after saving to db", sku, inventory[0]?.sku)
            }
        }
    }
}
