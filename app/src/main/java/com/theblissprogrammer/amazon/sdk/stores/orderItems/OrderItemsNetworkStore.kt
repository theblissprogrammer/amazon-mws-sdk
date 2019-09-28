package com.theblissprogrammer.amazon.sdk.stores.orderItems

import com.theblissprogrammer.amazon.sdk.common.*
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetworkAsync
import com.theblissprogrammer.amazon.sdk.network.APIRouter
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.network.APISessionType
import com.theblissprogrammer.amazon.sdk.parsers.ListOrderItemsXmlParser
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.ListOrderItems


/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
class OrderItemsNetworkStore(val apiSession: APISessionType): OrderItemsStore {

    override fun fetchAsync(id: String): DeferredResult<ListOrderItems> {
        return coroutineNetworkAsync <ListOrderItems> {
            val response = apiSession.request(router = APIRouter.ReadOrderItems(id))

            // Handle errors
            val value = response.value
            if (value == null || !response.isSuccess) {
                val error = response.error

                return@coroutineNetworkAsync if (error != null) {
                    val exception = initDataError(response.error)
                    LogHelper.e(messages = *arrayOf("An error occurred while fetching order items: " +
                            "${error.description}."))
                    Result.failure(exception)
                } else {
                    Result.failure(DataError.UnknownReason(null))
                }
            }

            try {
                // Parse response data
                val listOrderItems = ListOrderItemsXmlParser().parse(value.data)
                Result.success(listOrderItems)
            } catch(e: Exception) {
                LogHelper.e(messages = *arrayOf("An error occurred while parsing order items: " +
                        "${e.localizedMessage ?: ""}."))
                Result.failure(DataError.ParseFailure(e))
            }
        }
    }

    override fun fetchNextAsync(nextToken: String): DeferredResult<ListOrderItems> {
        return coroutineNetworkAsync <ListOrderItems> {
            val response = apiSession.request(router = APIRouter.ReadNextOrderItems(nextToken))

            // Handle errors
            val value = response.value
            if (value == null || !response.isSuccess) {
                val error = response.error

                return@coroutineNetworkAsync  if (error != null) {
                    val exception = initDataError(response.error)
                    LogHelper.e(
                        messages = *arrayOf(
                            "An error occurred while fetching order items by next token: " +
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
                val listOrderItems = ListOrderItemsXmlParser().parse(value.data)
                    Result.success(listOrderItems)
            } catch (e: Exception) {
                LogHelper.e(
                    messages = *arrayOf(
                        "An error occurred while parsing order items by next token: " +
                                "${e.localizedMessage ?: ""}."
                    )
                )
                Result.failure(DataError.ParseFailure(e))
            }
        }
    }
}