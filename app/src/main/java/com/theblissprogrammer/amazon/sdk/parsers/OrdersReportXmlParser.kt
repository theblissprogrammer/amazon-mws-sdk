package com.theblissprogrammer.amazon.sdk.parsers

import android.util.Xml
import com.theblissprogrammer.amazon.sdk.enums.OrderStatus
import com.theblissprogrammer.amazon.sdk.enums.marketplaceFromSalesChannel
import com.theblissprogrammer.amazon.sdk.extensions.*
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.OrderItem
import com.theblissprogrammer.amazon.sdk.stores.orderItems.models.PriceComponent
import com.theblissprogrammer.amazon.sdk.stores.orders.models.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by ahmedsaad on 2018-02-20.
 * Copyright © 2017. All rights reserved.
 */
class OrdersReportXmlParser {
    // We don't use namespaces
    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(input: String): List<Order> {
        input.byteInputStream().use { inputStream ->
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()

            return parser.findChildTag("Message") {
                parser.findChildTag("Order") {
                    readOrder(parser)
                }.firstOrNull()
            }.filterNotNull()
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readOrder(parser: XmlPullParser): Order? {
        parser.require(XmlPullParser.START_TAG, ns, "Order")

        var id: String? = null
        var purchasedDate: Date? = null
        var updatedDate: Date? = null
        var status: OrderStatus = OrderStatus.Pending
        var salesChannel: String? = null
        var fulfillmentData: FulfillmentData? = null
        val items: ArrayList<OrderItem> = arrayListOf()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name
            when (name) {
                "AmazonOrderID" -> id = parser.readString(name)
                "PurchaseDate" -> purchasedDate = parser.readDate(name)
                "LastUpdatedDate" -> updatedDate = parser.readDate(name)
                "OrderStatus" -> status = OrderStatus.valueOf(parser.readString(name))
                "SalesChannel" -> salesChannel = parser.readString(name)
                "FulfillmentData" -> {
                    fulfillmentData = parser.findChildTag("Address") {
                        FulfillmentData(address = readAddress(parser))
                    }.firstOrNull()
                }
                "OrderItem" -> items.add(readOrderItem(parser))
                else -> parser.skip()
            }
        }

        if (id == null || purchasedDate == null || updatedDate == null) { return null}

        return Order(
                id = id,
                purchasedAt = purchasedDate,
                updatedAt = updatedDate,
                status = status,
                marketplace = marketplaceFromSalesChannel(salesChannel)
        )
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readAddress(parser: XmlPullParser): OrderAddress {
        parser.require(XmlPullParser.START_TAG, ns, "Address")

        var city: String? = null
        var state: String? = null
        var postalCode: String? = null
        var country: String? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name
            when (name) {
                "City" -> city = parser.readString(name)
                "State" -> state = parser.readString(name)
                "PostalCode" -> postalCode = parser.readString(name)
                "Country" -> country = parser.readString(name)
                else -> parser.skip()
            }
        }

        return OrderAddress(
                city = city,
                state = state,
                postalCode = postalCode,
                country = country
        )
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