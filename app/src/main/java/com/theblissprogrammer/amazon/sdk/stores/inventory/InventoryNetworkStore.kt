package com.theblissprogrammer.amazon.sdk.stores.inventory

import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.initDataError
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetworkAsync
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.network.APIRouter
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.InventoryModels
import com.theblissprogrammer.amazon.sdk.network.APISessionType
import com.theblissprogrammer.amazon.sdk.parsers.ListInventorySupplyXmlParser
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.ListInventorySupply

/**
 * Created by ahmedsaad on 2018-08-07.
 * Copyright (c) 2018. All rights reserved.
 **/
class InventoryNetworkStore(val apiSession: APISessionType): InventoryStore {

    override fun fetchAsync(request: InventoryModels.Request): DeferredResult<ListInventorySupply> {
        return coroutineNetworkAsync <ListInventorySupply> {
            val response = apiSession.request(router = APIRouter.ReadInventory(request))

            // Handle errors
            val value = response.value
            if (value == null || !response.isSuccess) {
                val error = response.error

                return@coroutineNetworkAsync if (error != null) {
                    val exception = initDataError(response.error)
                    LogHelper.e(messages = *arrayOf("An error occurred while fetching inventory: " +
                            "${error.description}."))
                    Result.failure(exception)
                } else {
                    Result.failure(DataError.UnknownReason(null))
                }
            }

            try {
                // Parse response data
                val listInventorySupply = ListInventorySupplyXmlParser().parse(value.data)
                Result.success(listInventorySupply)
            } catch(e: Exception) {
                LogHelper.e(messages = *arrayOf("An error occurred while parsing inventory: " +
                        "${e.localizedMessage ?: ""}."))
                Result.failure(DataError.ParseFailure(e))
            }
        }
    }

    override fun fetchNextAsync(nextToken: String): DeferredResult<ListInventorySupply> {
        return coroutineNetworkAsync <ListInventorySupply> {
            val response = apiSession.request(router = APIRouter.ReadNextInventory(nextToken))

            // Handle errors
            val value = response.value
            if (value == null || !response.isSuccess) {
                val error = response.error

                return@coroutineNetworkAsync  if (error != null) {
                    val exception = initDataError(response.error)
                    LogHelper.e(
                        messages = *arrayOf(
                            "An error occurred while fetching inventory by next token: " +
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
                val listInventorySupply = ListInventorySupplyXmlParser().parse(value.data)
                Result.success(listInventorySupply)
            } catch (e: Exception) {
                LogHelper.e(
                    messages = *arrayOf(
                        "An error occurred while parsing inventory by next token: " +
                                "${e.localizedMessage ?: ""}."
                    )
                )
                Result.failure(DataError.ParseFailure(e))
            }
        }
    }
}