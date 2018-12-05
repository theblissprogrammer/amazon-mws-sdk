package com.theblissprogrammer.amazon.sdk

import com.theblissprogrammer.amazon.sdk.enums.SecurityProperty
import com.theblissprogrammer.amazon.sdk.security.SecurityStore
import com.theblissprogrammer.amazon.sdk.security.SecurityWorkerType

/**
 * Created by ahmed.saad on 2018-12-03.
 * Copyright © 2018. All rights reserved.
 */
class MockSecurityWorker(private val store: SecurityStore): SecurityWorkerType {
    override fun get(key: SecurityProperty): String? {
        return store.get(key)
    }

    override fun set(key: SecurityProperty, value: String?): Boolean {
        return store.set(key, value = value)
    }

    override fun delete(key: SecurityProperty): Boolean {
        return store.delete(key)
    }

    override fun clear() {
        store.clear()
    }

    // For testing purposes we assume Security worker is functioning.
    override fun createKey(alias: String) { }
    override fun deleteKey(alias: String) { }
    override fun encrypt(value: String?): String? { return null }
    override fun decrypt(value: String?): String? { return null }

}