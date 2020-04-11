package com.theblissprogrammer.amazon.sdk.parsers

import android.util.Xml
import com.theblissprogrammer.amazon.sdk.enums.ReportStatus
import com.theblissprogrammer.amazon.sdk.enums.ReportType
import com.theblissprogrammer.amazon.sdk.extensions.findChildTag
import com.theblissprogrammer.amazon.sdk.extensions.readString
import com.theblissprogrammer.amazon.sdk.extensions.skip
import com.theblissprogrammer.amazon.sdk.stores.reports.models.RequestReport
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException


/**
 * Created by ahmedsaad on 2018-02-20.
 * Copyright © 2017. All rights reserved.
 */
class RequestReportXmlParser {
    // We don't use namespaces
    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(input: String): RequestReport? {
        input.byteInputStream().use { inputStream ->
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()

            return parser.findChildTag("RequestReportResult") {
                parser.findChildTag("ReportRequestInfo") {
                    readRequest(parser)
                }.firstOrNull()
            }.firstOrNull()
        }
    }


    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readRequest(parser: XmlPullParser): RequestReport? {
        parser.require(XmlPullParser.START_TAG, ns, "ReportRequestInfo")

        var id: String? = null
        var type: String? = null
        var status: ReportStatus? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name

            when (name) {
                "ReportRequestId" -> id = parser.readString(name)
                "ReportType" -> type = parser.readString(name)
                "ReportProcessingStatus" -> status = ReportStatus.valueOf(parser.readString(name))
                else -> parser.skip()
            }
        }

        if (id == null) { return null}

        return RequestReport(
                requestID = id,
                type = ReportType.value(type),
                status = status
        )
    }
}