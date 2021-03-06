package com.theblissprogrammer.amazon.sdk

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.theblissprogrammer.amazon.sdk.TestCredentials.Companion.sellerID
import com.theblissprogrammer.amazon.sdk.data.AppDatabase
import com.theblissprogrammer.amazon.sdk.dependencies.HasDependencies
import com.theblissprogrammer.amazon.sdk.access.MwsSdk
import com.theblissprogrammer.amazon.sdk.data.MIGRATION_1_2
import com.theblissprogrammer.amazon.sdk.data.MIGRATION_2_3
import com.theblissprogrammer.amazon.sdk.data.MIGRATION_3_4
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellerDAO
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellersCacheStore
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.SellerModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Created by ahmed.saad on 2018-12-04.
 * Copyright © 2018. All rights reserved.
 */
@RunWith(AndroidJUnit4::class)
class SellerUnitTests: HasDependencies {
    private lateinit var sellerDao: SellerDAO
    private lateinit var db: AppDatabase

    private val sellersWorker by lazy {
        dependencies.resolveSellersWorker
    }

    private val sellersRoomStore: SellersCacheStore by lazy {
        dependencies.resolveSellersCacheStore
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
        sellerDao = db.sellerDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun saving_and_fetching_live_data() {
        val id = "1234"
        val marketplace = MarketplaceType.UK
        val seller = Seller(id = id, marketplace = marketplace)

        runBlocking {

            val sellerLD = withContext(Dispatchers.IO) {
                sellerDao.insert(seller)
        sellerDao.fetchAllSellers()
            }

            val sellers = getValue(sellerLD)
            Assert.assertEquals("The number of sellers must equal to number added.", 1, sellers.size)
            Assert.assertEquals("The seller ids should match after saving to db", id, sellers[0].id)
            Assert.assertEquals("The seller marketplaces should match after saving to db", marketplace, sellers[0].marketplace)
        }
    }


    @Test
    @Throws(Exception::class)
    fun saving_and_fetching_from_db() {
        val id = "1234"
        val marketplace = MarketplaceType.UK
        val seller = Seller(id = id, marketplace = marketplace)

        runBlocking {

            val sellerDB = getValue(withContext(Dispatchers.IO) {
                sellerDao.insert(seller)
                sellerDao.fetch(id = id, marketplace = marketplace)
            })

            Assert.assertEquals("The seller ids should match after saving to db", id, sellerDB?.id)
            Assert.assertEquals("The seller marketplaces should match after saving to db", marketplace, sellerDB?.marketplace)
        }
    }

    @Test
    @Throws(Exception::class)
    fun saving_and_fetching_from_cache_store() {
        val id = "1234"
        val marketplace = MarketplaceType.UK
        val seller = Seller(id = id, marketplace = marketplace)

        runBlocking {
            val sellerDB = sellersRoomStore.createOrUpdate(seller).await().value

            Assert.assertNotNull("Seller should not be null", sellerDB)


            val seller = getValue(sellerDB)
            Assert.assertEquals("The seller ids should match after saving to db", id, seller?.id)
            Assert.assertEquals("The seller marketplaces should match after saving to db", marketplace, seller?.marketplace)
        }
    }

    @Test
    @Throws(Exception::class)
    fun saving_and_fetching_from_worker() {
        val id = "1234"
        val marketplace = MarketplaceType.UK
        val seller = Seller(id = id, marketplace = marketplace)

        runBlocking {
            sellersRoomStore.createOrUpdate(seller).await()

            sellersWorker.fetchSellerAsync(request = SellerModels.Request(
                id = id,
                marketplace = marketplace
            )) {
                Assert.assertTrue(
                    "An error occurred when there should not be: ${it.error?.localizedMessage ?: it.error}",
                    it.isSuccess
                )
                Assert.assertNull("Login should return a null error.", it.error)

                val seller = getValue(it.value)
                Assert.assertNotNull("Login should return valid seller object.", seller)
                Assert.assertEquals("The seller ids should match after saving to db", id, seller?.id)
                Assert.assertEquals("The seller marketplaces should match after saving to db", marketplace, seller?.marketplace)
            }
        }
    }


    @Test
    fun fetch_current_seller() {
        runBlocking {
            sellersWorker.fetchCurrentSellerAsync {
                Assert.assertTrue(
                    "An error occurred when there should not be: ${it.error?.localizedMessage ?: it.error}",
                    it.isSuccess
                )
                Assert.assertNull("Login should return a null error.", it.error)

                val seller = getValue(it.value)
                Assert.assertNotNull("Login should return valid seller object.", seller)
                Assert.assertEquals("The seller ids should match after saving to db", sellerID, seller?.id)
            }
        }
    }

}
