package com.theblissprogrammer.amazon.sdk.account

import android.content.Context
import com.theblissprogrammer.amazon.sdk.account.models.AccountModels
import com.theblissprogrammer.amazon.sdk.account.models.LoginModels
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.theblissprogrammer.amazon.sdk.data.SyncWorkerType
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellersCacheStore
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.failure
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.success
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys.Companion.sellerID
import com.theblissprogrammer.amazon.sdk.enums.SecurityProperty
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.ACTION_AUTHENTICATION_DID_LOGIN
import com.theblissprogrammer.amazon.sdk.extensions.ACTION_AUTHENTICATION_DID_LOGOUT
import com.theblissprogrammer.amazon.sdk.extensions.SELLER_ID
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.security.SecurityWorkerType
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


/**
 * Created by ahmedsaad on 2017-11-03.
 * Copyright © 2017. All rights reserved.
 */

class AuthenticationWorker(val service: AuthenticationService,
                           val preferencesWorker: PreferencesWorkerType,
                           val securityWorker: SecurityWorkerType,
                           val syncWorker: SyncWorkerType,
                           val context: Context?,
                           val sellersCacheStore: SellersCacheStore
) : AuthenticationWorkerType {

    /// Determine if user is authenticated on server and local level
    override val isAuthorized
            get() = service.isAuthorized && !preferencesWorker.get(sellerID).isNullOrEmpty()

    /// Pings remote service to verify authorization still valid.
    override suspend fun pingAuthorization(completion: CompletionResponse<Void>) {
        completion(service.pingAuthorization().await())
    }

    override suspend fun login(request: LoginModels.Request,
                       completion: CompletionResponse<AccountModels.Response>) {
        // Validate input
        if (request.sellerID.isBlank() ||
                request.token.isBlank()) {
           return completion(failure(DataError.Incomplete))
        }
        val response = service.login(request).await()

        val value = response.value
        if (value?.sellers == null || value.sellers.isEmpty() || !response.isSuccess) {
            completion(failure(response.error))
            return
        }

        if (value.token.isEmpty()) {
            LogHelper.e(messages = *arrayOf("Could not extract authorization token from login response."))
            completion(failure(DataError.Unauthorized))
            return
        }

        val default = value.sellers.first { it.marketplace == request.marketplace }
        authenticated(default, token = value.token) {
            /// Save sellers to cache
            value.sellers.forEach {seller ->  sellersCacheStore.createOrUpdate(seller).await() }

            // Notify application authentication occurred and ready
            if (context != null){
                val intent = Intent()
                intent.action = ACTION_AUTHENTICATION_DID_LOGIN
                intent.putExtra(SELLER_ID, it.value?.seller?.id)

                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            }

            LogHelper.i(messages = *arrayOf("Login complete for user #${value.sellers.first().id}."))

            completion(success(it.value))
        }
    }

    override fun logout() {
        // Save logged out user ID before cleared out
        val sellerID = preferencesWorker.get(sellerID)

        LogHelper.d(messages = *arrayOf("Log out for user #${sellerID ?: ""} begins..."))

        securityWorker.clear()
        preferencesWorker.clear()
        syncWorker.clear()

        LogHelper.i(messages = *arrayOf("Log out complete for user #${sellerID ?: 0}."))

        // Notify application authentication occurred and ready
        val context = context ?: return

        val intent = Intent()
        intent.action = ACTION_AUTHENTICATION_DID_LOGOUT
        intent.putExtra(SELLER_ID, sellerID ?: "")

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    /// Used for handling successfully completed signup or login
    private suspend fun authenticated(seller: Seller, token: String, completion: suspend (Result<AccountModels.Response>) -> Unit){

        // Clear previous tasks if any
        syncWorker.clear()

        // Store user info for later use on app start and db initialization
        securityWorker.set(key = SecurityProperty.TOKEN(seller.marketplace.region.name), value = token)
        preferencesWorker.set(seller.id, key = sellerID)
        preferencesWorker.set(seller.marketplace.name, key = DefaultsKeys.marketplace)

        completion(success(AccountModels.Response(seller = seller)))

        // Load remote data locally before proceeding
        syncWorker.configure()
        /*syncWorker.remotePull {
            completion(success(AccountModels.Response(seller = seller)))
        }*/
    }
}