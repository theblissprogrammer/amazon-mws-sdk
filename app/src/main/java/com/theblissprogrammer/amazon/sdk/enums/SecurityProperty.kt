package com.theblissprogrammer.amazon.sdk.enums

/**
 * Created by ahmedsaad on 2017-11-16.
 * Copyright © 2017. All rights reserved.
 */

sealed class SecurityProperty(val id: String) {
    class TOKEN(val region: String = "NA"): SecurityProperty("AUTH_TOKEN_$region")
    internal object DEFAULT_TOKEN: SecurityProperty("DEFAULT_TOKEN")
    object EMAIL: SecurityProperty("EMAIL")
    object PASSWORD: SecurityProperty("PASSWORD")
}