package com.theblissprogrammer.amazon.sdk.parsers

import android.util.Xml
import com.theblissprogrammer.amazon.sdk.stores.orders.models.*
import com.theblissprogrammer.amazon.sdk.extensions.findChildTag
import com.theblissprogrammer.amazon.sdk.extensions.readAttribute
import com.theblissprogrammer.amazon.sdk.extensions.readString
import com.theblissprogrammer.amazon.sdk.extensions.skip
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException


/**
 * Created by ahmedsaad on 2018-02-20.
 * Copyright © 2017. All rights reserved.
 */
class ListOrderItemsXmlParser {
    // We don't use namespaces
    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(input: String): List<OrderItem>? {
        input.byteInputStream().use { inputStream ->
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()

            return parser.findChildTag("ListOrderItemsResult") {
                parser.findChildTag("OrderItems") {
                    parser.findChildTag("OrderItem") {
                        readOrderItem(parser)
                    }.filterNotNull()
                }.firstOrNull()
            }.firstOrNull()
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readOrderItem(parser: XmlPullParser): OrderItem {
        parser.require(XmlPullParser.START_TAG, ns, "OrderItem")

        var asin: String? = null
        var sku: String? = null
        var productName: String? = null
        var quantity: String? = null
        var priceComponent: PriceComponent? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name
            when (name) {
                "ASIN" -> asin = parser.readString(name)
                "SKU" -> sku = parser.readString(name)
                "ProductName" -> productName = parser.readString(name)
                "Quantity" -> quantity = parser.readString(name)
                "ItemPrice" -> {
                    priceComponent = parser.findChildTag("Component") {
                        readPriceComponent(parser)
                    }.firstOrNull { it.type == "Principal" }
                }
                else -> parser.skip()
            }
        }

        return OrderItem(
                asin = asin,
                sku = sku,
                productName = productName,
                quantity = quantity?.toInt(),
                currency = priceComponent?.currency,
                price = priceComponent?.amount
        )
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readPriceComponent(parser: XmlPullParser): PriceComponent {
        parser.require(XmlPullParser.START_TAG, ns, "Component")

        var type: String? = null
        var amount: String? = null
        var currency: String? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name
            when (name) {
                "Type" -> type = parser.readString(name)
                "Amount" -> {
                    currency = parser.readAttribute(name, "currency")
                    amount = parser.readString(name)
                }
                else -> parser.skip()
            }
        }

        return PriceComponent(
                type = type,
                amount = amount?.toDouble(),
                currency = currency
        )
    }
}