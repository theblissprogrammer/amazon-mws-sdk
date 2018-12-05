package com.theblissprogrammer.amazon.sdk

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.theblissprogrammer.amazon.sdk.TestCredentials.Companion.sellerID
import com.theblissprogrammer.amazon.sdk.data.AppDatabase
import com.theblissprogrammer.amazon.sdk.dependencies.DependencyConfigurator
import com.theblissprogrammer.amazon.sdk.dependencies.HasDependencies
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellerDAO
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellersCacheStore
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellersWorkerType
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.SellerModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

/**
 * Created by ahmed.saad on 2018-12-04.
 * Copyright © 2018. All rights reserved.
 */
@RunWith(RobolectricTestRunner::class)
class SellerUnitTests: HasDependencies, DependencyConfigurator {
    private lateinit var sellerDao: SellerDAO
    private lateinit var db: AppDatabase

    private val sellersWorker: SellersWorkerType by lazy {
        dependencies.resolveSellersWorker
    }

    private val sellersRoomStore: SellersCacheStore by lazy {
        dependencies.resolveSellersCacheStore
    }

    @Before
    fun configure() {
        configure(application = ApplicationProvider.getApplicationContext(), dependencies = MockSDKDependency())
    }

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        sellerDao = db.sellerDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun `saving and fetching from db`() {
        val id = "1234"
        val marketplace = MarketplaceType.UK
        val seller = Seller(id = id, marketplace = marketplace)

        runBlocking {

            val sellerDB = async(Dispatchers.IO) {
                sellerDao.createSellers(seller)
                sellerDao.fetch(id = id, marketplace = marketplace)
            }.await()

            Assert.assertEquals("The seller ids should match after saving to db", id, sellerDB?.id)
            Assert.assertEquals("The seller marketplaces should match after saving to db", marketplace, sellerDB?.marketplace)
        }
    }

    @Test
    @Throws(Exception::class)
    fun `saving and fetching from cache store`() {
        val id = "1234"
        val marketplace = MarketplaceType.UK
        val seller = Seller(id = id, marketplace = marketplace)

        runBlocking {
            val sellerDB = sellersRoomStore.createOrUpdate(seller).await().value

            Assert.assertEquals("The seller ids should match after saving to db", id, sellerDB?.id)
            Assert.assertEquals("The seller marketplaces should match after saving to db", marketplace, sellerDB?.marketplace)
        }
    }

    @Test
    @Throws(Exception::class)
    fun `saving and fetching from worker`() {
        val id = "1234"
        val marketplace = MarketplaceType.UK
        val seller = Seller(id = id, marketplace = marketplace)

        runBlocking {
            sellersRoomStore.createOrUpdate(seller).await()

            sellersWorker.fetch(request = SellerModels.Request(
                id = id,
                marketplace = marketplace
            )) {
                Assert.assertTrue(
                    "An error occurred when there should not be: ${it.error?.localizedMessage ?: it.error}",
                    it.isSuccess
                )
                Assert.assertNull("Login should return a null error.", it.error)

                val seller = it.value
                Assert.assertNotNull("Login should return valid seller object.", seller)
                Assert.assertEquals("The seller ids should match after saving to db", id, seller?.id)
                Assert.assertEquals("The seller marketplaces should match after saving to db", marketplace, seller?.marketplace)
            }
        }
    }


    @Test
    fun `login and fetch current seller`() {
        // Login
        AccountUnitTests().`account login valid`()

        runBlocking {
            sellersWorker.fetchCurrent {
                Assert.assertTrue(
                    "An error occurred when there should not be: ${it.error?.localizedMessage ?: it.error}",
                    it.isSuccess
                )
                Assert.assertNull("Login should return a null error.", it.error)

                val seller = it.value
                Assert.assertNotNull("Login should return valid seller object.", seller)
                Assert.assertEquals("The seller ids should match after saving to db", sellerID, seller?.id)
            }
        }
    }

}
