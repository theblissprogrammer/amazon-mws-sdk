package com.theblissprogrammer.amazon.sdk.stores.orders

import com.theblissprogrammer.amazon.sdk.common.*
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetwork
import com.theblissprogrammer.amazon.sdk.network.APIRouter
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.network.APISessionType
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderModels
import com.theblissprogrammer.amazon.sdk.parsers.ListOrdersXmlParser
import com.theblissprogrammer.amazon.sdk.stores.orders.models.ListOrders


/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
class OrdersNetworkStore(val apiSession: APISessionType): OrdersStore {

    override fun fetchAsync(request: OrderModels.Request): DeferredResult<ListOrders> {
        return coroutineNetwork <ListOrders> {
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
                val listOrders = ListOrdersXmlParser().parse(value.data)
                Result.success(listOrders)
            } catch(e: Exception) {
                LogHelper.e(messages = *arrayOf("An error occurred while parsing orders: " +
                        "${e.localizedMessage ?: ""}."))
                Result.failure(DataError.ParseFailure(e))
            }
        }
    }

    override fun fetchNextAsync(nextToken: String): DeferredResult<ListOrders> {
        return coroutineNetwork <ListOrders> {
            val response = apiSession.request(router = APIRouter.ReadNextOrders(nextToken))

            // Handle errors
            val value = response.value
            if (value == null || !response.isSuccess) {
                val error = response.error

                return@coroutineNetwork  if (error != null) {
                    val exception = initDataError(response.error)
                    LogHelper.e(
                        messages = *arrayOf(
                            "An error occurred while fetching orders: " +
                                    "${error.description}."
                        )
                    )
                    Result.failure(exception)
                } else {
                    Result.failure(DataError.UnknownReason(null))
                }
            }

             try {
                // Parse response data
                val listOrders = ListOrdersXmlParser().parse(value.data)
                    Result.success(listOrders)
            } catch (e: Exception) {
                LogHelper.e(
                    messages = *arrayOf(
                        "An error occurred while parsing orders by next token: " +
                                "${e.localizedMessage ?: ""}."
                    )
                )
                Result.failure(DataError.ParseFailure(e))
            }
        }
    }
}