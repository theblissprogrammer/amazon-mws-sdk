package com.theblissprogrammer.amazon.sdk.stores.subscriptions

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.policy.Policy
import com.amazonaws.auth.policy.Principal
import com.amazonaws.auth.policy.Resource
import com.amazonaws.auth.policy.Statement
import com.amazonaws.auth.policy.actions.SQSActions
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.CreateQueueRequest
import com.amazonaws.services.sqs.model.QueueAttributeName
import com.amazonaws.services.sqs.model.QueueNameExistsException
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest
import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.success
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
                val create = sqs.createQueue(createRequest)

                val getQueueAttributesResult = sqs.getQueueAttributes(create.queueUrl,
                        listOf(QueueAttributeName.QueueArn.toString()))
                val queueArn = getQueueAttributesResult.attributes[QueueAttributeName.QueueArn.toString()]

                val allAccessPolicy = Policy("SQSAllAccess", listOf(
                        Statement(Statement.Effect.Allow)
                                .withActions(SQSActions.AllSQSActions)
                                .withPrincipals(Principal.All)
                                .withId("SQSAllAccessStatement")
                                .withResources(Resource(queueArn))
                ))

                sqs.setQueueAttributes(SetQueueAttributesRequest()
                        .withQueueUrl(create.queueUrl)
                        .withAttributes(mapOf(QueueAttributeName.Policy.toString() to allAccessPolicy.toJson())))

                create.queueUrl

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