package com.theblissprogrammer.amazon.sdk.data

/**
 * Created by ahmedsaad on 2017-12-01.
 * Copyright © 2017. All rights reserved.
 */
class DataWorker(private val store: DataStore): DataWorkerType {

    override fun delete(sellerID: String) {
        store.delete(sellerID)
    }

    override fun configure() = store.configure()
}