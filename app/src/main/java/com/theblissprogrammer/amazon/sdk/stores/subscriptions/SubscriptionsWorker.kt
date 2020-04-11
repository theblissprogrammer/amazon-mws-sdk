package com.theblissprogrammer.amazon.sdk.stores.subscriptions

import android.os.Handler
import com.theblissprogrammer.amazon.sdk.common.*
import com.theblissprogrammer.amazon.sdk.enums.*
import com.theblissprogrammer.amazon.sdk.extensions.*
import com.theblissprogrammer.amazon.sdk.network.NetworkBoundResource
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.stores.reports.ReportsWorkerType
import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.Queue
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.ReportsProcessingNotificationPayload
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.SubscriptionsModels
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by ahmed.saad on 2019-09-26.
 * Copyright © 2019. All rights reserved.
 */
class SubscriptionsWorker(
        val store: SubscriptionsStore,
        val cacheStore: SubscriptionsCacheStore,
        val preferencesWorker: PreferencesWorkerType,
        val reportsWorker: ReportsWorkerType): SubscriptionsWorkerType {

    private val marketplace by lazy {
        MarketplaceType.valueOf(preferencesWorker.get(DefaultsKeys.marketplace) ?: "US")
    }

    override fun getQueue(completion: LiveResourceResponse<Queue>) {
        val id = preferencesWorker.get(DefaultsKeys.sellerID)

        if (id.isNullOrBlank()) {
            //completion(failure(DataError.Unauthorized))
            return
        }

        val queueName = "$id-$marketplace"

        val request = SubscriptionsModels.QueueRequest(
                name = queueName,
                marketplace = marketplace
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

    override fun registerDestination(request: SubscriptionsModels.DestinationRequest, completion: ResourceResponse<Void>) {
        coroutineOnUi {
            val data = coroutineBackgroundAsync {
                store.registerDestination(request).asResource()
            }.await()

            completion(data)
        }
    }

    override fun createSubscription(request: SubscriptionsModels.SubscriptionRequest, completion: ResourceResponse<Void>?) {
        coroutineOnUi {
            val data = coroutineBackgroundAsync {
                store.createSubscription(request).asResource()
            }.await()

            completion?.invoke(data)
        }
    }

    override fun pollQueue(request: SubscriptionsModels.PollRequest) {
        coroutineOnIO {
            val data = coroutineBackgroundAsync {
                store.pollQueue(request)
            }.await()

            data.value?.forEach {
                if (it.payload == null) return@forEach

                when (it.metaData?.notificationType) {
                    NotificationType.ReportProcessingFinished -> {
                        val payload = it.payload as ReportsProcessingNotificationPayload

                        if (payload.status == ReportStatus._DONE_ && !payload.reportId.isNullOrBlank()) {
                            val readRequest = ReportModels.ReadRequest(
                                    id = payload.reportId,
                                    type = payload.type,
                                    marketplace = marketplace,
                                    requestId = payload.reportRequestId
                            )

                            reportsWorker.processReport(readRequest)
                        } else {
                            reportsWorker.fetchReportRequest(ReportModels.ReportRequest(
                                    statuses = listOf(ReportStatus._DONE_),
                                    types = listOf(payload.type)
                            )) {

                                val lastReport = it.value?.firstOrNull()
                                if (!lastReport?.reportID.isNullOrBlank()) {
                                    val readRequest = ReportModels.ReadRequest(
                                            id = lastReport?.reportID ?: "",
                                            type = payload.type,
                                            marketplace = marketplace,
                                            requestId = payload.reportRequestId

                                    )
                                    reportsWorker.processReport(readRequest)
                                }
                            }
                        }

                    }
                    else -> {}
                }
            }
        }
    }

    override fun startPolling() {
        getQueue {
            coroutineOnUi {
                it.observeForever {
                    val queue = it.data ?: return@observeForever

                    // Register and create subscription if not done so
                    registerDestination(SubscriptionsModels.DestinationRequest(queue)) {
                        createSubscription(SubscriptionsModels.SubscriptionRequest(queue, notificationType = NotificationType.ReportProcessingFinished))
                    }

                    pollRequest = SubscriptionsModels.PollRequest(queue)
                }
            }
        }
    }

    override fun stopPolling() {
        handler.removeCallbacks(mRunnableCode)
        polling.set(false)
    }

    private var polling: AtomicBoolean = AtomicBoolean(false)

    private val handler by lazy {
        Handler()
    }

    private var pollRequest: SubscriptionsModels.PollRequest? = null
        set(value) {
            field = value

            if (value != null)
                runPolling()
        }

    // alternate the view's background color
    private val mRunnableCode = object : Runnable {
        override fun run() {
            pollRequest?.let { pollQueue(it) }
            handler.postDelayed(this, 5000)
        }
    }

    private fun runPolling() {
        reportsWorker.getReports {
            coroutineOnUi {
                it.value?.observeForever {
                    if (it.isNullOrEmpty()) {
                        // stop polling
                        handler.removeCallbacks(mRunnableCode)

                        polling.set(false)
                    } else {
                        // start polling if not already
                        if (!polling.getAndSet(true))
                            mRunnableCode.run()
                    }
                }
            }
        }
    }

}