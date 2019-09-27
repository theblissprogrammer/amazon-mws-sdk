package com.theblissprogrammer.amazon.sdk.stores.subscriptions

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.CreateQueueRequest
import com.amazonaws.services.sqs.model.QueueNameExistsException
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.success
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys
import com.theblissprogrammer.amazon.sdk.enums.marketplaceFromId
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetwork
import com.theblissprogrammer.amazon.sdk.preferences.ConstantsType
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.Queue
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.SubscriptionsModels
import java.lang.Exception

/**
 * Created by ahmed.saad on 2019-09-26.
 * Copyright © 2019. All rights reserved.
 */
class SubscriptionsNetworkStore(val constants: ConstantsType): SubscriptionsStore {

    override fun getQueue(request: SubscriptionsModels.Request): DeferredResult<Queue> {

        return coroutineNetwork <Queue> {

            val credentials = BasicAWSCredentials(constants.sqsAwsAccessKeyID, constants.sqsAwsSecretKey)

            val sqs = AmazonSQSClient(credentials)
            val createRequest = CreateQueueRequest(request.name)
                    .addAttributesEntry("ReceiveMessageWaitTimeSeconds", "20")

            val queueUrl = try {
                sqs.createQueue(createRequest).queueUrl
            } catch (e: QueueNameExistsException) {
                sqs.getQueueUrl(request.name).queueUrl
            } catch (e: Exception) {
                throw e
            }

            val queue = Queue(
                    name = request.name,
                    url = queueUrl,
                    marketplace = request.marketplace
            )

            success(queue)
        }

    }
}