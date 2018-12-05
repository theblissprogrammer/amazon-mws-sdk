package com.theblissprogrammer.amazon.sdk.extensions

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/

@Throws(XmlPullParserException::class, IOException::class)
fun <T> XmlPullParser.findChildTag(tag: String, call: () -> T): ArrayList<T> {
    val res: ArrayList<T> = arrayListOf()
    while (next() != XmlPullParser.END_TAG) {
        if (eventType != XmlPullParser.START_TAG) {
            continue
        }

        // Starts by looking for the item tag
        if (name == tag) {
            res.add(call())
        } else {
            skip()
        }
    }

    return res
}



// Processes title tags in the feed.
@Throws(IOException::class, XmlPullParserException::class)
fun XmlPullParser.readString(tag: String): String {
    require(XmlPullParser.START_TAG, namespace, tag)
    val id = readText()
    require(XmlPullParser.END_TAG, namespace, tag)
    return id
}

// For the tags title and summary, extracts their text values.
@Throws(IOException::class, XmlPullParserException::class)
fun XmlPullParser.readDate(tag: String): Date {
    require(XmlPullParser.START_TAG, namespace, tag)
    val pubDate = readText()

    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.US)

    val date = formatter.parse(pubDate)
    require(XmlPullParser.END_TAG, namespace, tag)
    return date
}

// Processes summary tags in the feed.
@Throws(IOException::class, XmlPullParserException::class)
fun XmlPullParser.readAttribute(tag: String, attribute: String): String {
    require(XmlPullParser.START_TAG, namespace, tag)
    return getAttributeValue(null, attribute)
}

// For the tags title and summary, extracts their text values.
@Throws(IOException::class, XmlPullParserException::class)
fun XmlPullParser.readText(): String {
    var result = ""
    if (next() == XmlPullParser.TEXT) {
        result = text
        nextTag()
    }
    return result
}

@Throws(XmlPullParserException::class, IOException::class)
fun XmlPullParser.skip() {
    if (eventType != XmlPullParser.START_TAG) {
        throw IllegalStateException()
    }
    var depth = 1
    while (depth != 0) {
        when (next()) {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}