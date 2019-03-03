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
class OrderItemUnitTests: HasDependencies {
    private lateinit var orderItemDao: OrderItemDAO
    private lateinit var db: AppDatabase

    private val ordersWorker by lazy {
        dependencies.resolveOrdersWorker
    }

    private val orderItemsWorker by lazy {
        dependencies.resolveOrderItemsWorker
    }

    private val orderItemsRoomStore: OrderItemsCacheStore by lazy {
        dependencies.resolveOrderItemsCacheStore
    }

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun configureSdk() {
        MwsSdk.configure(
            application = InstrumentationRegistry.getTargetContext().applicationContext as Application,
            dependencies = MockSDKDependency()
        )
        AccountUnitTests().account_login_valid()
    }

    @Before
    fun createDb() {
        val context: Context = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .fallbackToDestructiveMigration().build()
        orderItemDao = db.orderItemDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun saving_and_fetching_db() {
        val id = "1234"
        val orderItem = OrderItem(orderItemId = id, orderId = "")

        runBlocking {
            val orders = withContext(Dispatchers.IO) {
                orderItemDao.insert(orderItem)
                orderItemDao.fetchAllOrderItems()
            }

            Assert.assertEquals("The number of order items must equal to number added.", 1, orders.size)
            Assert.assertEquals("The number of order item id must equal to the one inserted..", id, orders[0].orderItemId)
        }
    }

    @Test
    fun fetch_order_items_by_id() {
        runBlocking {
            val id = "114-8859035-8931444"
            orderItemsWorker.fetch(request = OrderItemModels.Request(
                ids = listOf(id)
            )) {
                Assert.assertTrue(
                    "An error occurred when there should not be: ${it.error?.localizedMessage ?: it.error}",
                    it.isSuccess
                )
                Assert.assertNull("Order should return a null error.", it.error)

                val orderItems = getValue(it.value)
                Assert.assertNotNull("Worker should return valid order object.", orderItems)
                Assert.assertEquals("The order ids should match after saving to db", id, orderItems[0]?.orderId)
            }
        }
    }

    @Test
    fun fetch_order_items() {
        runBlocking {
            val request = OrderModels.Request(
                //startDate = Date().add(Calendar.DATE, -1).startOfDay(),
                //endDate = Date().add(Calendar.DATE, -1).endOfDay(),
                marketplaces = listOf(MarketplaceType.US)
            )
            ordersWorker.fetch(request) {
                val ids = getValue(it.value).map { it.id }

                runBlocking {
                    orderItemsWorker.fetch(
                        request = OrderItemModels.Request(
                            ids = ids
                        )
                    ) {
                        Assert.assertTrue(
                            "An error occurred when there should not be: ${it.error?.localizedMessage ?: it.error}",
                            it.isSuccess
                        )
                        Assert.assertNull("Order should return a null error.", it.error)

                        val orderItems = getValue(it.value)
                        Assert.assertNotNull("Worker should return valid order object.", orderItems)
                    }
                }
            }
        }
    }
}
