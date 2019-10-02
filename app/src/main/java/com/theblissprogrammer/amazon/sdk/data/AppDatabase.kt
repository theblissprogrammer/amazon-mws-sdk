package com.theblissprogrammer.amazon.sdk.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.theblissprogrammer.amazon.sdk.stores.details.DetailDAO
import com.theblissprogrammer.amazon.sdk.stores.details.models.Detail
import com.theblissprogrammer.amazon.sdk.stores.inventory.InventoryDAO
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.orderItems.OrderItemDAO
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.OrderItem
import com.theblissprogrammer.amazon.sdk.stores.orders.OrderDAO
import com.theblissprogrammer.amazon.sdk.stores.orders.models.Order
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderAddress
import com.theblissprogrammer.amazon.sdk.stores.products.ProductDAO
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellerDAO
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.SubscriptionsDAO
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.Queue

/**
 * Created by ahmed.saad on 2018-12-03.
 * Copyright © 2018. All rights reserved.
 */
@Database(
    entities = [
        Seller::class,
        Order::class,
        OrderAddress::class,
        OrderItem::class,
        Inventory::class,
        Product::class,
        Detail::class,
        Queue::class
    ],
    version = 15
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sellerDao(): SellerDAO
    abstract fun orderDao(): OrderDAO
    abstract fun orderItemDao(): OrderItemDAO
    abstract fun inventoryDao(): InventoryDAO
    abstract fun productDao(): ProductDAO
    abstract fun detailDao(): DetailDAO
    abstract fun subscriptionsDAO(): SubscriptionsDAO
}