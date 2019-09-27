package com.theblissprogrammer.amazon.sdk.stores.subscriptions

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sqs.AmazonSQSAsyncClient
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.CreateQueueRequest
import com.amazonaws.services.sqs.model.QueueNameExistsException
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.common.LiveCompletionResponse
import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.common.LiveResult.Companion.failure
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.marketplaceFromId
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.Queue
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.SubscriptionsModels
import java.lang.Exception

/**
 * Created by ahmed.saad on 2019-09-26.
 * Copyright © 2019. All rights reserved.
 */
class SubscriptionsWorker(
        val store: SubscriptionsStore,
        val cacheStore: SubscriptionsCacheStore,
        val preferencesWorker: PreferencesWorkerType): SubscriptionsWorkerType {

    override suspend fun getQueue(completion: LiveCompletionResponse<Queue>) {
        val id = preferencesWorker.get(DefaultsKeys.sellerID)
        val marketplace = preferencesWorker.get(DefaultsKeys.marketplace)

        if (id.isNullOrBlank() || marketplace.isNullOrBlank()) {
            completion(failure(DataError.Unauthorized))
            return
        }

        val queueName = "$id-$marketplace"

        val request = SubscriptionsModels.Request(
                name = queueName,
                marketplace = marketplaceFromId(marketplace) ?: MarketplaceType.US
        )

        // Use cache storage if applicable
        val cache = cacheStore.getQueue(request = request).await()

        // Retrieve missing cache data from cloud if applicable
        if (cache.error != null && cache.error === DataError.NonExistent) {
            val response = this.store.getQueue(request = request).await()
            val value = response.value

            return if (value == null || !response.isSuccess) {
                completion(failure(response.error))
            } else {
                completion(cacheStore.createOrUpdateQueue(value).await())
            }
        }

        // Immediately return local response
        completion(cache)

        val cacheElement = cache.value
        if (cacheElement == null || !cache.isSuccess) {
            return
        }

        val response = this.store.getQueue(request = request).await()

        // Validate if any updates that needs to be stored
        val element = response.value
        if (element == null || !response.isSuccess) {
            return
        }

        // Update local storage with updated data
        val savedElement = cacheStore.createOrUpdateQueue(element).await()

        if (!savedElement.isSuccess) {
            LogHelper.e(messages = *arrayOf("Could not save updated queue locally" +
                    " from remote storage: ${savedElement.error?.localizedMessage ?: ""}"))
        }

    }
}