package com.theblissprogrammer.amazon.sdk.stores.subscriptions.models

import com.theblissprogrammer.amazon.sdk.enums.NotificationType
import com.theblissprogrammer.amazon.sdk.enums.ReportStatus
import com.theblissprogrammer.amazon.sdk.enums.ReportType
import java.util.*

/**
 * Created by ahmed.saad on 2019-10-01.
 * Copyright © 2019. All rights reserved.
 */
data class Notification <T> (
        val metaData: NotificationMetaData?,
        val payload: T?)

data class NotificationMetaData(
        val notificationType: NotificationType,
        val payloadVersion: String?,
        val uniqueId: String,
        val publishTime: Date?,
        val sellerId: String)

data class ReportsProcessingNotificationPayload(
        val sellerId: String,
        val reportRequestId: String,
        val reportId: String,
        val type: ReportType,
        val status: ReportStatus)