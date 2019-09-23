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
class ProductUnitTests: HasDependencies {
    private lateinit var db: AppDatabase

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

    @Before
    fun createDb() {
        val context: Context = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .fallbackToDestructiveMigration().build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    val asins = listOf(
        "B00062NVRQ",
        "B00062NVSA",
        "B000CEMWHI",
        "B000FNCYGK",
        "B000GG85F0",
        "B000H68J2S",
        "B000UVW4JE",
        "B001IAEO1M",
        "B001JH5VH0",
        "B001JHBCZ0",
        "B0027Z4XF4",
        "B002CCY1MC",
        "B002KANWU8",
        "B002YQU3AA",
        "B0034KYA36",
        "B0036L4EHU",
        "B0038BSKT6",
        "B003ES2HNI",
        "B003IGB74Q",
        "B003LNQQ9W",
        "B003TBWPJQ",
        "B003TN56JK",
        "B0046OFVK0",
        "B004AI7G3W",
        "B004AIC6BY",
        "B004AIC7EU",
        "B004M32XWE",
        "B004N0YM1G",
        "B004V31FPG",
        "B0054RU3Q0",
        "B0056I9G9M",
        "B005J514OW",
        "B0060A2SBI",
        "B00620O38M",
        "B006R62UZO",
        "B008BTRKZU",
        "B008V4B2Q8",
        "B00C1K9OA4",
        "B00C7DCZR4",
        "B00CWAAL00",
        "B00E3GXZJA",
        "B00E4MMWTC",
        "B00E4MNT9O",
        "B00EMEX3FE",
        "B00ERF6X4G",
        "B00ERFBX9G",
        "B00EX69EXQ",
        "B00FFSLF10",
        "B00GB9AWXO",
        "B00GSXQIN6",
        "B00GT0G5JA",
        "B00GTXCMCG",
        "B00HMTFR3W",
        "B00JZ246S6",
        "B00K6OSDYA",
        "B00KFA94FC",
        "B00KS9HEK2",
        "B00LFN9Q4W",
        "B00MAXPBD6",
        "B00MB3JOOM",
        "B00MTO7BS4",
        "B00MXOVBO0",
        "B00NP280XY",
        "B00ONLVO9I",
        "B00OO8K6PI",
        "B00P1S9FRA",
        "B00PHK7EHU",
        "B00PRBAP9S",
        "B00RV6OKJ8",
        "B00SP2K370",
        "B00SQW5KFO",
        "B00T55VPLE",
        "B00TIYZSMO",
        "B00UVAHDBS",
        "B00V557TOO",
        "B00VC44HM0",
        "B00XHMA5Z8",
        "B00XQFE502",
        "B00Z7R60KK",
        "B0105YWYTK",
        "B012TTUHI4",
        "B013RIIDVO",
        "B014SHGRVQ",
        "B014SI38TO",
        "B014SL9UHU",
        "B014VB6OYO",
        "B0192FZMGK",
        "B019WU426G",
        "B019WU42A2",
        "B01AVJ4384",
        "B01AVK2A38",
        "B01B14NDGQ",
        "B01B29PCUU",
        "B01CFF9H4C",
        "B01E6NRM3E",
        "B01EGLYYEQ",
        "B01ERZ3N7A",
        "B01EZ64QJU",
        "B01F44WDFQ",
        "B01GEU5LZS",
        "B01JZXMPT6",
        "B01KYWADIW",
        "B01L94785U",
        "B01M3UNAWI",
        "B01M6CEO2G",
        "B01MQ6AJ4N",
        "B01MRGNP5U",
        "B01MRGNRC0",
        "B01MXHPNCL",
        "B06X97QQNW",
        "B06XHT3192",
        "B06XKPHBYQ",
        "B06XPJPKRM",
        "B06XR6KFM2",
        "B06XZ9CGCN",
        "B06Y4PT64W",
        "B06ZYSYY5J",
        "B071JZ8CS3",
        "B071KVWN1L",
        "B071R3KXKP",
        "B0722VVNLQ",
        "B072DXWJ48",
        "B074ZV88T7",
        "B075WXN5B3",
        "B07629VC5D",
        "B0762FM7S3",
        "B076B3D8HG",
        "B076CQV8LP",
        "B076GSB214",
        "B076ZKTDKN",
        "B07752QT3N",
        "B07755ZM9Y",
        "B0778XHMKL",
        "B077GLNQV8",
        "B077YNRSB2",
        "B0781YXH8S",
        "B0785ZRWZT",
        "B079C4PCSV",
        "B079TG2TVS",
        "B07BHC7P86",
        "B07BVYZL88",
        "B07BWKYGK1",
        "B07CDN39DP",
        "B07D74L4TL",
        "B07FPZZMMH",
        "B07GM37PJ2",
        "B07GRQT459",
        "B07GSJJHQB",
        "B07J16HHCF",
        "B07LBYJMSP",
        "B07NZ47P8D",
        "B07P6JL7TW",
        "B07PBZRDJN",
        "B07PRY9KSP")

    @Test
    @Throws(Exception::class)
    fun saving_and_fetching_db() {
        runBlocking {
            detailsWorker.fetchProductDetails(asins) {

                val details = getValue(it.value)

                Assert.assertNotNull("Worker should return valid details object.", details)
            }
        }
    }
}
