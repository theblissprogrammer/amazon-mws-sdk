package com.theblissprogrammer.amazon.sdk.extensions

import com.theblissprogrammer.amazon.sdk.stores.orders.models.PriceTotal
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ahmedsaad on 2018-08-06.
 * Copyright (c) 2018. All rights reserved.
 **/

@Throws(XmlPullParserException::class, IOException::class)
fun <T> XmlPullParser.findChildTag(tag: List<String>, call: () -> T): ArrayList<T> {
    val res: ArrayList<T> = arrayListOf()
    while (next() != XmlPullParser.END_TAG) {
        if (eventType != XmlPullParser.START_TAG) {
            continue
        }

        // Starts by looking for the item tag
        if (tag.contains(name)) {
            res.add(call())
        } else {
            skip()
        }
    }

    return res
}

fun <T> XmlPullParser.findChildTag(tag: String, call: () -> T): ArrayList<T> {
    return findChildTag(listOf(tag), call)
}

@Throws(XmlPullParserException::class, IOException::class)
fun <T> XmlPullParser.findChildTagAsync(tag: List<String>, call: () -> T) {
    while (next() != XmlPullParser.END_TAG) {
        if (eventType != XmlPullParser.START_TAG) {
            continue
        }

        // Starts by looking for the item tag
        if (tag.contains(name)) {
            call()
        } else {
            skip()
        }
    }
}

fun <T> XmlPullParser.findChildTagAsync(tag: String, call: () -> T) {
    findChildTagAsync(listOf(tag), call)
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

    val formatter = dateFormatter()

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

// Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
@Throws(XmlPullParserException::class, IOException::class)
fun XmlPullParser.readPrice(tag: String): PriceTotal {
    require(XmlPullParser.START_TAG, namespace, tag)

    var currency: String? = null
    var amount: Double? = null

    while (next() != XmlPullParser.END_TAG) {
        if (eventType != XmlPullParser.START_TAG) {
            continue
        }

        when (name) {
            "CurrencyCode" -> currency = readString(name)
            "Amount" -> amount = readString(name).toDoubleOrNull()
            else -> skip()
        }
    }

    return PriceTotal(
        currencyCode = currency,
        amount = amount
    )
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