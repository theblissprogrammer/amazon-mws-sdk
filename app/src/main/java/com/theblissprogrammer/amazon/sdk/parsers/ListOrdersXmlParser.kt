package com.theblissprogrammer.amazon.sdk.parsers

import android.util.Xml
import com.theblissprogrammer.amazon.sdk.enums.FulfillmentChannel
import com.theblissprogrammer.amazon.sdk.enums.OrderStatus
import com.theblissprogrammer.amazon.sdk.enums.marketplaceFromSalesChannel
import com.theblissprogrammer.amazon.sdk.extensions.*
import com.theblissprogrammer.amazon.sdk.stores.orders.models.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.*


/**
 * Created by ahmedsaad on 2018-02-20.
 * Copyright © 2017. All rights reserved.
 */
class ListOrdersXmlParser {
    // We don't use namespaces
    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(input: String): ListOrders? {
        input.byteInputStream().use { inputStream ->
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()

            val orders = arrayListOf<ListOrder?>()
            var nextToken: String? = null

            parser.findChildTagAsync(listOf("ListOrdersResult", "ListOrdersByNextTokenResult")) {
                parser.findChildTagAsync(listOf("Orders", "NextToken")) {
                    if (parser.name == "Orders") {
                        parser.findChildTagAsync("Order") {
                            orders.add(readOrder(parser))
                        }
                    } else {
                        nextToken = parser.readString("NextToken")
                    }
                }
            }

            return ListOrders(orders = orders.filterNotNull(), nextToken = nextToken)
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
        var fulfillmentChannel: FulfillmentChannel? = null
        var fulfillmentData: FulfillmentData? = null
        var orderTotal: OrderTotal? = null
        var numberOfItemsShipped: Int? = null
        var numberOfItemsUnshipped: Int? = null

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
                "FulfillmentChannel" -> fulfillmentChannel = FulfillmentChannel.valueOf(parser.readString(name))
                "NumberOfItemsShipped" -> numberOfItemsShipped = parser.readString(name).toIntOrNull()
                "NumberOfItemsUnshipped" -> numberOfItemsUnshipped = parser.readString(name).toIntOrNull()
                "OrderTotal" -> {
                    orderTotal = readOrderTotal(parser)
                }
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
            marketplace = marketplaceFromSalesChannel(salesChannel),
            numberOfItems = (numberOfItemsShipped ?: 0) + (numberOfItemsUnshipped ?: 0),
            currency = orderTotal?.currencyCode,
            amount = orderTotal?.amount,
            fulfillmentChannel = fulfillmentChannel ?: FulfillmentChannel.MFN
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

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readOrderTotal(parser: XmlPullParser): OrderTotal {
        parser.require(XmlPullParser.START_TAG, ns, "OrderTotal")

        var currency: String? = null
        var amount: Double? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name
            when (name) {
                "CurrencyCode" -> currency = parser.readString(name)
                "Amount" -> amount = parser.readString(name).toDoubleOrNull()
                else -> parser.skip()
            }
        }

        return OrderTotal(
            currencyCode = currency,
            amount = amount
        )
    }
}