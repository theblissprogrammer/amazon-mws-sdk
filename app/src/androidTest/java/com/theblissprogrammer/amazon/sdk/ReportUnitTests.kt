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
import com.theblissprogrammer.amazon.sdk.enums.ReportType
import com.theblissprogrammer.amazon.sdk.export.Shopify
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
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product
import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
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
class ReportUnitTests: HasDependencies {

    private val reportsWorker by lazy {
        dependencies.resolveReportsWorker
    }

    private val detailsWorker by lazy {
        dependencies.resolveDetailsWorker
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

    @Test
    fun fetch_products() {
        runBlocking {
            reportsWorker.fetchReport<Product>(ReportModels.Request(
                type = ReportType.AllListings,
                marketplaces = listOf(MarketplaceType.US)
            )) {
                Assert.assertTrue(
                    "An error occurred when there should not be: ${it.error?.localizedMessage ?: it.error}",
                    it.isSuccess
                )
                Assert.assertNull("Products should return a null error.", it.error)

                Assert.assertNotNull("Products should return valid product object.", it.value)
            }
        }
    }

    @Test
    fun fetch_product_details() {
        runBlocking {
            detailsWorker.fetchProductDetails(listOf()) {
                Assert.assertTrue(
                    "An error occurred when there should not be: ${it.error?.localizedMessage ?: it.error}",
                    it.isSuccess
                )
                Assert.assertNull("Products should return a null error.", it.error)

                Assert.assertNotNull("Products should return valid product object.", it.value)

                val productDetails = getValue(it.value)

                Shopify.export(productDetails.toList())
            }
        }
    }
}
