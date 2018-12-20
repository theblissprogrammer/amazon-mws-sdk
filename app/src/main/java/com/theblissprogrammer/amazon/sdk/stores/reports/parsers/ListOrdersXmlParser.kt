package com.theblissprogrammer.amazon.sdk.stores.reports.parsers

import android.util.Xml
import com.theblissprogrammer.amazon.sdk.enums.OrderStatus
import com.theblissprogrammer.amazon.sdk.enums.marketplaceFromSalesChannel
import com.theblissprogrammer.amazon.sdk.extensions.*
import com.theblissprogrammer.amazon.sdk.stores.orders.models.*
import com.theblissprogrammer.amazon.sdk.extensions.findChildTag
import com.theblissprogrammer.amazon.sdk.extensions.readDate
import com.theblissprogrammer.amazon.sdk.extensions.readString
import com.theblissprogrammer.amazon.sdk.extensions.skip
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by ahmedsaad on 2018-02-20.
 * Copyright © 2017. All rights reserved.
 */
class ListOrdersXmlParser {
    // We don't use namespaces
    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(input: String): ListOrders {
        input.byteInputStream().use { inputStream ->
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()

            return parser.findChildTag("ListOrdersResult") {
                val orders = parser.findChildTag("Orders") {
                    parser.findChildTag("Order") {
                        readOrder(parser)
                    }.filterNotNull()
                }.firstOrNull() ?: listOf()
                ListOrders(orders = orders, nextToken = null)
            }.first()
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readOrder(parser: XmlPullParser): ListOrder? {
        parser.require(XmlPullParser.START_TAG, ns, "Order")

        var id: String? = null
        var purchasedDate: Date? = null
        var updatedDate: Date? = null
        var status: OrderStatus = OrderStatus.Pending
        var salesChannel: String? = null
        var email: String? = null
        var fulfillmentData: FulfillmentData? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name
            when (name) {
                "AmazonOrderId" -> id = parser.readString(name)
                "PurchaseDate" -> purchasedDate = parser.readDate(name)
                "LastUpdateDate" -> updatedDate = parser.readDate(name)
                "OrderStatus" -> status = OrderStatus.valueOf(parser.readString(name))
                "SalesChannel" -> salesChannel = parser.readString(name)
                "BuyerEmail" -> email = parser.readString(name)
                "ShippingAddress" -> {
                    fulfillmentData = FulfillmentData(address = readAddress(parser))
                }
                else -> parser.skip()
            }
        }

        if (id == null || purchasedDate == null || updatedDate == null) { return null}

        if (email != null) fulfillmentData?.address?.email = email

        // Set the order id for the address
        fulfillmentData?.address?.orderId = id

        val order = Order(
                id = id,
                purchasedAt = purchasedDate,
                updatedAt = updatedDate,
                status = status,
                marketplace = marketplaceFromSalesChannel(salesChannel)
        )

        return ListOrder(
            order = order,
            buyer = fulfillmentData?.address
        )
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readAddress(parser: XmlPullParser): OrderAddress {
        parser.require(XmlPullParser.START_TAG, ns, "ShippingAddress")

        var city: String? = null
        var state: String? = null
        var postalCode: String? = null
        var country: String? = null
        var buyerName: String? = null
        var addressLine1: String? = null
        var addressLine2: String? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name
            when (name) {
                "City" -> city = parser.readString(name)
                "StateOrRegion" -> state = parser.readString(name)
                "PostalCode" -> postalCode = parser.readString(name)
                "CountryCode" -> country = parser.readString(name)
                "Name" -> buyerName = parser.readString(name)
                "AddressLine1" -> addressLine1 = parser.readString(name)
                "AddressLine2" -> addressLine2 = parser.readString(name)
                else -> parser.skip()
            }
        }

        return OrderAddress(
                city = city,
                state = state,
                postalCode = postalCode,
                country = country,
                name = buyerName,
                line1 = addressLine1,
                line2 = addressLine2
        )
    }
}