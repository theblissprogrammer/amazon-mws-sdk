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
import com.theblissprogrammer.amazon.sdk.stores.orders.OrderDAO
import com.theblissprogrammer.amazon.sdk.stores.orders.OrdersCacheStore
import com.theblissprogrammer.amazon.sdk.stores.orders.models.ListOrder
import com.theblissprogrammer.amazon.sdk.stores.orders.models.Order
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderModels
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellerDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

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
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build()
        orderDao = db.orderDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun saving_and_fetching_live_data() {

        runBlocking {
            val orders = async(Dispatchers.IO) {
                orderDao.fetchAllOrders()
            }.await()

            Assert.assertEquals("The number of sellers must equal to number added.", 1, orders.size)
        }
    }

    @Test
    fun fetch_orders() {
        runBlocking {
            val request = OrderModels.Request(
                //startDate = Date().add(Calendar.DATE, -5),
                marketplaces = listOf(MarketplaceType.US)
            )
            ordersWorker.fetch(request) {
                Assert.assertTrue(
                    "An error occurred when there should not be: ${it.error?.localizedMessage ?: it.error}",
                    it.isSuccess
                )
                Assert.assertNull("Orders should return a null error.", it.error)

                val orders = getValue(it.value)
                Assert.assertEquals("Orders should return valid order object.", 11, orders.size)
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun saving_and_fetching_from_worker() {
        val id = "1234"
        val marketplace = MarketplaceType.UK
        val order = Order(id = id, status = OrderStatus.Shipped, marketplace = marketplace)

        runBlocking {
            ordersRoomStore.createOrUpdate(ListOrder(order, buyer = null)).await()

            ordersWorker.fetch(request = OrderModels.Request(
                id = id,
                marketplaces = listOf(marketplace)
            )) {
                Assert.assertTrue(
                    "An error occurred when there should not be: ${it.error?.localizedMessage ?: it.error}",
                    it.isSuccess
                )
                Assert.assertNull("Order should return a null error.", it.error)

                val order = getValue(it.value)
                Assert.assertNotNull("Worker should return valid order object.", order)
                Assert.assertEquals("The order ids should match after saving to db", id, order[0]?.id)
            }
        }
    }

    @Test
    fun fetch_order_by_id() {
        runBlocking {
            val id = "114-0164853-7858633"
            val marketplace = MarketplaceType.US
            ordersWorker.fetch(request = OrderModels.Request(
                marketplaces = listOf(marketplace)
            )) {
                Assert.assertTrue(
                    "An error occurred when there should not be: ${it.error?.localizedMessage ?: it.error}",
                    it.isSuccess
                )
                Assert.assertNull("Order should return a null error.", it.error)

                val order = getValue(it.value)
                Assert.assertNotNull("Worker should return valid order object.", order)
                Assert.assertEquals("The order ids should match after saving to db", id, order[0]?.id)
            }
        }
    }
}
