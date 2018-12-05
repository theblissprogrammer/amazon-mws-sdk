package com.theblissprogrammer.amazon.sdk.errors

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
class ErrorsXmlParser {
    // We don't use namespaces
    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(input: String): List<String> {
        input.byteInputStream().use { inputStream ->
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()

            return parser.findChildTag("Error") {
                readRequest(parser)
            }.filterNotNull()
        }
    }


    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readRequest(parser: XmlPullParser): String? {
        parser.require(XmlPullParser.START_TAG, ns, "Error")

        var message: String? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name
            when (name) {
                "Message" -> message = parser.readString(name)
                else -> parser.skip()
            }
        }

        return message
    }
}