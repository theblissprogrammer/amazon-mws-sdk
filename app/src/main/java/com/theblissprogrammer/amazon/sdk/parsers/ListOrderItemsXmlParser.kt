package com.theblissprogrammer.amazon.sdk.parsers

import android.util.Xml
import com.theblissprogrammer.amazon.sdk.extensions.*
import com.theblissprogrammer.amazon.sdk.stores.orders.models.*
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
    fun parse(input: String): ListOrderItems {
        input.byteInputStream().use { inputStream ->
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()

            val orderItems = arrayListOf<OrderItem?>()
            var nextToken: String? = null
            var orderId: String? = null

            parser.findChildTagAsync(listOf("ListOrderItemsResult", "ListOrderItemsByNextTokenResult")) {
                parser.findChildTagAsync(listOf("OrderItems", "NextToken", "AmazonOrderId")) {
                    val name = parser.name
                    when (name) {
                        "OrderItems" -> parser.findChildTagAsync("OrderItem") {
                            orderItems.add(readOrderItem(parser))
                        }
                        "NextToken" -> nextToken = parser.readString(name)
                        "AmazonOrderId" -> orderId = parser.readString(name)
                    }
                }
            }

            return ListOrderItems(orderItems = orderItems.filterNotNull(), orderId = orderId, nextToken = nextToken)
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readOrderItem(parser: XmlPullParser): OrderItem? {
        parser.require(XmlPullParser.START_TAG, ns, "OrderItem")

        var orderItemId: String? = null
        var asin: String? = null
        var sku: String? = null
        var productName: String? = null
        var quantity: String? = null
        var priceComponent: PriceTotal? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name
            when (name) {
                "ASIN" -> asin = parser.readString(name)
                "SellerSKU" -> sku = parser.readString(name)
                "OrderItemId" -> orderItemId = parser.readString(name)
                "Title" -> productName = parser.readString(name)
                "QuantityOrdered" -> quantity = parser.readString(name)
                "ItemPrice" -> {
                    priceComponent = parser.readPrice(name)
                }
                else -> parser.skip()
            }
        }

        if (orderItemId == null) { return null }

        return OrderItem(
            orderItemId = orderItemId,
            asin = asin,
            sku = sku,
            productName = productName,
            quantity = quantity?.toInt(),
            currency = priceComponent?.currencyCode,
            price = priceComponent?.amount
        )
    }

}