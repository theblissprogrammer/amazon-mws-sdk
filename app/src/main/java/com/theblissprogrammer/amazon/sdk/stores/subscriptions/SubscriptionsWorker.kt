package com.theblissprogrammer.amazon.sdk.stores.subscriptions

import androidx.lifecycle.MutableLiveData
import com.theblissprogrammer.amazon.sdk.common.*
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.marketplaceFromId
import com.theblissprogrammer.amazon.sdk.extensions.switchMap
import com.theblissprogrammer.amazon.sdk.network.NetworkBoundResource
import com.theblissprogrammer.amazon.sdk.network.Resource
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.Queue
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.SubscriptionsModels

/**
 * Created by ahmed.saad on 2019-09-26.
 * Copyright © 2019. All rights reserved.
 */
class SubscriptionsWorker(
        val store: SubscriptionsStore,
        val cacheStore: SubscriptionsCacheStore,
        val preferencesWorker: PreferencesWorkerType): SubscriptionsWorkerType {

    override fun getQueue(completion: LiveResourceResponse<Queue>) {
        val id = preferencesWorker.get(DefaultsKeys.sellerID)
        val marketplace = preferencesWorker.get(DefaultsKeys.marketplace)

        if (id.isNullOrBlank() || marketplace.isNullOrBlank()) {
            //completion(failure(DataError.Unauthorized))
            return
        }

        val queueName = "$id-$marketplace"

        val request = SubscriptionsModels.QueueRequest(
                name = queueName,
                marketplace = marketplaceFromId(marketplace) ?: MarketplaceType.US
        )

        val data = object : NetworkBoundResource<Queue, Queue>() {
            override fun saveCallResult(item: Queue?) {
                if (item != null) {
                    cacheStore.createOrUpdateQueue(item)
                }
            }

            override fun shouldFetch(data: Queue?): Boolean {
                return data == null || data.url.isNullOrBlank() || data.updatedAt == null
            }

            override fun loadFromDb(): LiveResult<Queue> {
                return cacheStore.getQueue(request = request)
            }

            override fun createCall(): Result<Queue> {
                return store.getQueue(request = request)
            }

        }.asLiveData()

        completion(data)

    }

    fun registerDestination(completion: LiveResourceResponse<Void>) {
        getQueue {
             val data = it.switchMap { resource ->

                 if (resource?.data?.url == null) {
                     val data = MutableLiveData<Resource<Void>>()

                     return@switchMap data
                 }

                 val request = SubscriptionsModels.DestinationRequest(
                        url = resource.data.url,
                        marketplace = resource.data.marketplace
                 )

                 return@switchMap object : NetworkBoundResource<Void, Void>() {
                     override fun saveCallResult(item: Void?) {

                     }

                     override fun shouldFetch(data: Void?): Boolean {
                         return true
                     }

                     override fun loadFromDb(): LiveResult<Void> {
                         val data = MutableLiveData<Void>().apply { value = null }
                         return LiveResult.success(data)
                     }

                     override fun createCall(): Result<Void> {
                         return store.registerDestination(request)
                     }

                 }.asLiveData()
            }
        }

    }
}