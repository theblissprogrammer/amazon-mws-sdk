package com.theblissprogrammer.amazon.sdk.stores.subscriptions.models

import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.NotificationType

/**
 * Created by ahmed.saad on 2019-09-27.
 * Copyright © 2019. All rights reserved.
 */
sealed class SubscriptionsModels {
    class QueueRequest(
            val name: String,
            val marketplace: MarketplaceType): SubscriptionsModels()

    class DestinationRequest(
            val queue: Queue): SubscriptionsModels()

    class SubscriptionRequest(
            val queue: Queue,
            val notificationType: NotificationType,
            val isEnabled: Boolean = true): SubscriptionsModels()

    class PollRequest(
            val queue: Queue): SubscriptionsModels()
}