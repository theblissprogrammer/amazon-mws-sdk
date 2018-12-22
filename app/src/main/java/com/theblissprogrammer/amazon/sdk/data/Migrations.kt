package com.theblissprogrammer.amazon.sdk.data

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

/**
 * Created by ahmed.saad on 2018-12-19.
 * Copyright © 2018. All rights reserved.
 */
val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create the new table
        database.execSQL(
            "CREATE TABLE `Order` (`id` TEXT NOT NULL, `purchasedAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `status` TEXT NOT NULL, `marketplace` TEXT, PRIMARY KEY(`id`))")
        database.execSQL("CREATE INDEX index_Order_purchasedAt ON `Order` (purchasedAt)")
        database.execSQL("DROP INDEX index_Seller_name")
        /*
        //Alter table
        database.execSQL("ALTER TABLE users ADD COLUMN last_update INTEGER")

        // Copy the data
        database.execSQL(
            "INSERT INTO users_new (userid, username, last_update) SELECT userid, username, last_update FROM users")

        // Remove the old table
        database.execSQL("DROP TABLE users")

        // Change the table name to the correct one
        database.execSQL("ALTER TABLE users_new RENAME TO users")*/
    }
}

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create the new table
        //Alter table
        database.execSQL("ALTER TABLE `Order` ADD COLUMN numberOfItems INTEGER")
        database.execSQL("ALTER TABLE `Order` ADD COLUMN fulfillmentChannel TEXT")
        database.execSQL("ALTER TABLE `Order` ADD COLUMN currency TEXT")
        database.execSQL("ALTER TABLE `Order` ADD COLUMN amount REAL")

        /*// Copy the data
        database.execSQL(
            "INSERT INTO users_new (userid, username, last_update) SELECT userid, username, last_update FROM users")

        // Remove the old table
        database.execSQL("DROP TABLE users")

        // Change the table name to the correct one
        database.execSQL("ALTER TABLE users_new RENAME TO users")*/
    }
}

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create the new table
        //Alter table
        database.execSQL(
            "CREATE TABLE `OrderAddress` (`orderId` TEXT NOT NULL, `name` TEXT, `line1` TEXT, `line2` TEXT, `city` TEXT, `state` TEXT, `postalCode` TEXT, `country` TEXT, `email` TEXT, FOREIGN KEY(orderId) REFERENCES `Order`(id) ON DELETE CASCADE, PRIMARY KEY(`orderId`))")


        /*// Copy the data
        database.execSQL(
            "INSERT INTO users_new (userid, username, last_update) SELECT userid, username, last_update FROM users")

        // Remove the old table
        database.execSQL("DROP TABLE users")

        // Change the table name to the correct one
        database.execSQL("ALTER TABLE users_new RENAME TO users")*/
    }
}