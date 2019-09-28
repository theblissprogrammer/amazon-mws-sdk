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
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.success
import com.theblissprogrammer.amazon.sdk.common.initDataError
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.network.APIRouter
import com.theblissprogrammer.amazon.sdk.network.APISessionType
import com.theblissprogrammer.amazon.sdk.preferences.ConstantsType
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.Queue
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.SubscriptionsModels
import java.lang.Exception
import java.util.*


/**
 * Created by ahmed.saad on 2019-09-26.
 * Copyright © 2019. All rights reserved.
 */
class SubscriptionsNetworkStore(val constants: ConstantsType,
                                val apiSession: APISessionType): SubscriptionsStore {

    override fun getQueue(request: SubscriptionsModels.QueueRequest): Result<Queue> {
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
                marketplace = request.marketplace,
                updatedAt = Date()
        )

        return success(queue)

    }

    override fun registerDestination(request: SubscriptionsModels.DestinationRequest): Result<Void> {
        val response = apiSession.request(router = APIRouter.RegisterDestination(request))

        // Handle errors
        val value = response.value
        if (value == null || !response.isSuccess) {
            val error = response.error

            return if (error != null) {
                val exception = initDataError(response.error)
                LogHelper.e(messages = *arrayOf("An error occurred while registering destination: " +
                        "${error.description}."))
                Result.failure(exception)
            } else {
                Result.failure(DataError.UnknownReason(null))
            }
        }


        return success(null)
    }
}