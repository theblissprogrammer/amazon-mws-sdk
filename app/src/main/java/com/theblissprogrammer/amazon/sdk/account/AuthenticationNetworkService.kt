package com.theblissprogrammer.amazon.sdk.account

import com.theblissprogrammer.amazon.sdk.account.models.AccountModels
import com.theblissprogrammer.amazon.sdk.account.models.LoginModels
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetwork
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.ListMarketplaceParticipationsXmlParser
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.failure
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.success
import com.theblissprogrammer.amazon.sdk.common.initDataError
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.network.APIRouter
import com.theblissprogrammer.amazon.sdk.network.APISessionType
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import kotlinx.coroutines.Deferred

/**
 * Created by ahmedsaad on 2017-11-03.
 * Copyright © 2017. All rights reserved.
 */

class AuthenticationNetworkService(val apiSession: APISessionType,
                                   val preferencesWorker: PreferencesWorkerType) : AuthenticationService {

    /// Determines if the user is signed in with a token for authorized requests.
    override val isAuthorized
            get() = apiSession.isAuthorized

    /// Pings remote server to verify authorization still valid.
    override fun pingAuthorization(): Deferred<Result<Void>> {
        return coroutineNetwork <Void> {
            val response = apiSession.request(APIRouter.ReadUser())
            val error = initDataError(response.error)

            /* Only explicit unauthorized error will be checked */
            if (error != DataError.Unauthorized && error != DataError.BadRequest) {
                success()
            } else {
                failure(error)
            }
        }
    }

    override fun login(request: LoginModels.Request): Deferred<Result<AccountModels.ServerResponse>> {

        return coroutineNetwork <AccountModels.ServerResponse> {
            val response = apiSession.request(router = APIRouter.Login(request))

            // Handle errors
            val value = response.value
            if (value == null || !response.isSuccess) {
                val error = response.error

                return@coroutineNetwork if (error != null) {
                    val exception = initDataError(response.error)
                    LogHelper.e(messages = *arrayOf("An error occurred while fetching login: " +
                            "${error.description}."))
                    failure(exception)
                } else {
                    failure(DataError.UnknownReason(null))
                }
            }

            try {
                // Parse response data
                val sellers = ListMarketplaceParticipationsXmlParser().parse(value.data)?.map {
                    Seller(
                            id = it.sellerID,
                            marketplace = it.marketplaceType ?: MarketplaceType.US
                    )
                } ?: throw DataError.BadRequest

                val serverResponse = AccountModels.ServerResponse(
                        sellers = sellers,
                        token = request.token
                )

                success(serverResponse)
            } catch(e: Exception) {
                LogHelper.e(messages = *arrayOf("An error occurred while parsing login: " +
                        "${e.localizedMessage ?: ""}."))
                failure(DataError.ParseFailure(e))
            }
        }
    }
}