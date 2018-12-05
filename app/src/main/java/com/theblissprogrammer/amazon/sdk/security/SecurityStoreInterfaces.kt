package com.theblissprogrammer.amazon.sdk.security

import com.theblissprogrammer.amazon.sdk.enums.SecurityProperty

/**
 * Created by ahmedsaad on 2017-11-10.
 * Copyright © 2017. All rights reserved.
 */

interface SecurityStore {
    fun get(key: SecurityProperty): String?
    fun set(key: SecurityProperty, value: String?): Boolean
    fun delete(key: SecurityProperty): Boolean
    fun clear()
}

interface SecurityWorkerType: SecurityStore {
    fun createKey(alias: String)
    fun deleteKey(alias: String)
    fun encrypt(value: String?): String?
    fun decrypt(value: String?): String?
}