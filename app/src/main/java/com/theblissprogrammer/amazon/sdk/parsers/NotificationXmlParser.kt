package com.theblissprogrammer.amazon.sdk.parsers

import android.util.Xml
import com.theblissprogrammer.amazon.sdk.enums.NotificationType
import com.theblissprogrammer.amazon.sdk.enums.ReportStatus
import com.theblissprogrammer.amazon.sdk.enums.ReportType
import com.theblissprogrammer.amazon.sdk.extensions.*
import com.theblissprogrammer.amazon.sdk.stores.reports.models.RequestReport
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.Notification
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.NotificationMetaData
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.ReportsProcessingNotificationPayload
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.*


/**
 * Created by ahmedsaad on 2018-02-20.
 * Copyright © 2017. All rights reserved.
 */
class NotificationXmlParser {
    // We don't use namespaces
    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun <T> parse(input: String): Notification<T>? {
        input.byteInputStream().use { inputStream ->
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()

            var metaData: NotificationMetaData? = null
            var payload: T? = null

            parser.findChildTagAsync(listOf("NotificationMetaData", "NotificationPayload")) {
                val name = parser.name
                when (name) {
                    "NotificationMetaData" -> metaData = readMetaData(parser)
                    "NotificationPayload" -> {
                        when (metaData?.notificationType) {
                            NotificationType.ReportProcessingFinished -> parser.findChildTagAsync("ReportProcessingFinishedNotification") {
                                payload = readReportsProcessingPayload(parser) as? T
                            }
                            else -> { }
                        }

                    }
                }
            }

            return Notification(
                    metaData = metaData,
                    payload = payload
            )
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readMetaData(parser: XmlPullParser): NotificationMetaData? {
        parser.require(XmlPullParser.START_TAG, ns, "NotificationMetaData")

        var type: NotificationType? = null
        var version: String? = null
        var uniqueId: String? = null
        var time: Date? = null
        var sellerId: String? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name
            when (name) {
                "NotificationType" -> type = NotificationType.valueOf(parser.readString(name).removeSuffix("Notification"))
                "PayloadVersion" -> version = parser.readString(name)
                "UniqueId" -> uniqueId = parser.readString(name)
                "PublishTime" -> time = parser.readDate(name)
                "SellerId" -> sellerId = parser.readString(name)
                else -> parser.skip()
            }
        }

        if (uniqueId == null || type == null || sellerId == null) { return null}

        return NotificationMetaData(
                notificationType = type,
                payloadVersion = version,
                uniqueId = uniqueId,
                publishTime = time,
                sellerId = sellerId
        )
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readReportsProcessingPayload(parser: XmlPullParser): ReportsProcessingNotificationPayload? {
        parser.require(XmlPullParser.START_TAG, ns, "ReportProcessingFinishedNotification")

        var sellerId: String? = null
        var reportRequestId: String? = null
        var reportId: String? = null
        var type: ReportType? = null
        var status: ReportStatus? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name
            when (name) {
                "SellerId" -> sellerId = parser.readString(name)
                "ReportRequestId" -> reportRequestId = parser.readString(name)
                "ReportId" -> reportId = parser.readString(name)
                "ReportType" -> type = ReportType.value(parser.readString(name))
                "ReportProcessingStatus" -> status = ReportStatus.valueOf("_${parser.readString(name)}_")
                else -> parser.skip()
            }
        }

        if (type == null || sellerId == null || status == null || reportRequestId == null) return null

        return ReportsProcessingNotificationPayload(
                sellerId = sellerId,
                reportRequestId = reportRequestId,
                reportId = reportId,
                type = type,
                status = status
        )
    }
}