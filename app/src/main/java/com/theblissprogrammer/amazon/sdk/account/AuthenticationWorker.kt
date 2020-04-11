package com.theblissprogrammer.amazon.sdk.account

import android.content.Context
import com.theblissprogrammer.amazon.sdk.account.models.AccountModels
import com.theblissprogrammer.amazon.sdk.account.models.LoginModels
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.theblissprogrammer.amazon.sdk.stores.sync.SyncWorkerType
import com.theblissprogrammer.amazon.sdk.stores.sellers.SellersCacheStore
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.failure
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.success
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys.Companion.sellerID
import com.theblissprogrammer.amazon.sdk.enums.SecurityProperty
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.*
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.security.SecurityWorkerType
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller


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
    override fun pingAuthorization(completion: CompletionResponse<Void>) {
        coroutineOnUi {
            val data = coroutineBackgroundAsync {
                service.pingAuthorization()
            }.await()

            completion(data)
        }
    }

    override fun login(request: LoginModels.Request,
                       completion: CompletionResponse<AccountModels.Response>) {

        // Validate input
        if (request.sellerID.isBlank() ||
                request.token.isBlank()) {
           return completion(failure(DataError.Incomplete))
        }

        coroutineOnIO {
            val response = coroutineBackgroundAsync {
                service.login(request)
            }.await()

            val value = response.value
            if (value?.sellers == null || value.sellers.isEmpty() || !response.isSuccess) {
                coroutineOnUi {
                    completion(failure(response.error))
                }
                return@coroutineOnIO
            }

            if (value.token.isEmpty()) {
                LogHelper.e(messages = *arrayOf("Could not extract authorization token from login response."))
                coroutineOnUi {
                    completion(failure(DataError.Unauthorized))
                }
                return@coroutineOnIO
            }

            val default = value.sellers.first { it.marketplace == request.marketplace }

            // Clear previous tasks if any
            syncWorker.clear()

            // Store user info for later use on app start and db initialization
            securityWorker.set(key = SecurityProperty.TOKEN(default.marketplace.region.name), value = value.token)

            preferencesWorker.set(default.sellerId, key = sellerID)
            preferencesWorker.set(default.marketplace.name, key = DefaultsKeys.marketplace)

            // Load remote data locally before proceeding
            syncWorker.configure()

            /// Save sellers to cache
            sellersCacheStore.createOrUpdate(value.sellers)

            // Notify application authentication occurred and ready
            if (context != null){
                val intent = Intent()
                intent.action = ACTION_AUTHENTICATION_DID_LOGIN
                intent.putExtra(SELLER_ID, default.sellerId)

                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            }

            LogHelper.i(messages = *arrayOf("Login complete for user #${value.sellers.first().sellerId}."))

            coroutineOnUi {
                completion(success(AccountModels.Response(seller = default)))
            }

            syncWorker.remotePull(refresh = true)
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


    }
}