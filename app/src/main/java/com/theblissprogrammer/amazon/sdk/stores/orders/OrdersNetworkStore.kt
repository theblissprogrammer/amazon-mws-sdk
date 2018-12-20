package com.theblissprogrammer.amazon.sdk.stores.orders

import com.theblissprogrammer.amazon.sdk.common.*
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetwork
import com.theblissprogrammer.amazon.sdk.network.APIRouter
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.network.APISessionType
import com.theblissprogrammer.amazon.sdk.stores.orders.models.ListOrder
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderModels
import com.theblissprogrammer.amazon.sdk.stores.reports.parsers.ListOrdersXmlParser


/**
 * Created by ahmedsaad on 2018-08-05.
 * Copyright (c) 2018. All rights reserved.
 **/
class OrdersNetworkStore(val apiSession: APISessionType): OrdersStore {

    override fun fetch(request: OrderModels.Request, completion: SuspendCompletionResponse<List<ListOrder>>): DeferredResult<List<ListOrder>> {
        return coroutineNetwork <List<ListOrder>> {
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

                // Run the call back to save data to db
                completion(Result.success(listOrders?.orders))

                if (listOrders?.nextToken != null) {

                    fetchNext(listOrders.nextToken, orders = ArrayList(listOrders.orders), completion = completion)
                } else
                Result.success(listOrders?.orders)
            } catch(e: Exception) {
                LogHelper.e(messages = *arrayOf("An error occurred while parsing orders: " +
                        "${e.localizedMessage ?: ""}."))
                Result.failure(DataError.ParseFailure(e))
            }
        }
    }

    private suspend fun fetchNext(nextToken: String, orders: ArrayList<ListOrder>, completion: SuspendCompletionResponse<List<ListOrder>>)
            : Result<List<ListOrder>> {
        val response = apiSession.request(router = APIRouter.ReadNextOrders(nextToken))

        // Handle errors
        val value = response.value
        if (value == null || !response.isSuccess) {
            val error = response.error

            return if (error != null) {
                val exception = initDataError(response.error)
                LogHelper.e(messages = *arrayOf("An error occurred while fetching orders: " +
                        "${error.description}."))
                Result.failure(exception)
            } else {
                Result.failure(DataError.UnknownReason(null))
            }
        }

        return try {
            // Parse response data
            val listOrders = ListOrdersXmlParser().parse(value.data)
            orders.addAll(listOrders?.orders ?: listOf())

            // Run the call back to save data to db
            completion(Result.success(listOrders?.orders))

            if (listOrders?.nextToken != null) {
                fetchNext(listOrders.nextToken, orders = orders, completion = completion)
            } else
                Result.success(orders)
        } catch(e: Exception) {
            LogHelper.e(messages = *arrayOf("An error occurred while parsing orders by next token: " +
                    "${e.localizedMessage ?: ""}."))
            Result.failure(DataError.ParseFailure(e))
        }
    }
}