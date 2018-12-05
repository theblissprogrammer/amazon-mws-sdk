package com.theblissprogrammer.amazon.sdk.protocols


/**
 * Created by ahmedsaad on 2018-03-05.
 * Copyright © 2018. All rights reserved.
 */
interface AuthenticationServiceDelegate {
    fun authenticationDidLogin(sellerID: String)
    fun authenticationDidLogout(sellerID: String)
}