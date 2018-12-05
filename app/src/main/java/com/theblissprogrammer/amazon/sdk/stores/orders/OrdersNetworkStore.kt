package com.theblissprogrammer.amazon.sdk.stores.orders

import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetwork
import com.theblissprogrammer.amazon.sdk.network.APIRouter
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.initDataError
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.network.APISessionType
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderModels
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderType
import kotlinx.coroutines.Deferred

/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
class OrdersNetworkStore(val apiSession: APISessionType): OrdersStore {

    override fun fetch(request: OrderModels.Request): Deferred<Result<List<OrderType>>> {
        return coroutineNetwork <List<OrderType>> {
            val response = apiSession.request(router = APIRouter.ReadOrders(request))

            // Handle errors
            val value = response.value
            if (value == null || !response.isSuccess) {
                val error = response.error

                return@coroutineNetwork if (error != null) {
                    val exception = initDataError(response.error)
                    LogHelper.e(messages = *arrayOf("An error occurred while fetching orders: " +
                            "${error.description}."))
                    Result.failure(exception)
                } else {
                    Result.failure(DataError.UnknownReason(null))
                }
            }

            try {
                // Parse response data


                Result.success()
            } catch(e: Exception) {
                LogHelper.e(messages = *arrayOf("An error occurred while parsing login: " +
                        "${e.localizedMessage ?: ""}."))
                Result.failure(DataError.ParseFailure(e))
            }
        }
    }
}