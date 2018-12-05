package com.theblissprogrammer.amazon.sdk.stores.sellers.models

import android.util.Xml
import com.theblissprogrammer.amazon.sdk.extensions.findChildTag
import com.theblissprogrammer.amazon.sdk.extensions.readString
import com.theblissprogrammer.amazon.sdk.extensions.skip
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException


/**
 * Created by ahmedsaad on 2018-02-20.
 * Copyright © 2017. All rights reserved.
 */
class ListMarketplaceParticipationsXmlParser {
    // We don't use namespaces
    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(input: String): List<Participation>? {
        input.byteInputStream().use { inputStream ->
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()

            return parser.findChildTag("ListMarketplaceParticipationsResult") {
                parser.findChildTag("ListParticipations") {
                    parser.findChildTag("Participation") {
                        readRequest(parser)
                    }.filterNotNull()
                }.firstOrNull()
            }.firstOrNull()
        }
    }


    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readRequest(parser: XmlPullParser): Participation? {
        parser.require(XmlPullParser.START_TAG, ns, "Participation")

        var marketplaceId: String? = null
        var sellerId: String? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name
            when (name) {
                "MarketplaceId" -> marketplaceId = parser.readString(name)
                "SellerId" -> sellerId = parser.readString(name)
                else -> parser.skip()
            }
        }

        if (sellerId == null || marketplaceId == null) { return null}

        return Participation(
                marketplaceID = marketplaceId,
                sellerID = sellerId
        )
    }
}