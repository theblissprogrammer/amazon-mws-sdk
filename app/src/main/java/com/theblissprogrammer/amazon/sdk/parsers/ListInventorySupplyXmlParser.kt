package com.theblissprogrammer.amazon.sdk.parsers

import android.util.Xml
import com.theblissprogrammer.amazon.sdk.enums.InventoryCondition
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.SupplyType
import com.theblissprogrammer.amazon.sdk.enums.marketplaceFromId
import com.theblissprogrammer.amazon.sdk.extensions.*
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Inventory
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.ListInventorySupply
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.Quantity
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.QuantityType
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.lang.Exception


/**
 * Created by ahmedsaad on 2018-02-20.
 * Copyright © 2017. All rights reserved.
 */
class ListInventorySupplyXmlParser {
    // We don't use namespaces
    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(input: String): ListInventorySupply {
        input.byteInputStream().use { inputStream ->
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()

            val inventory = arrayListOf<Inventory?>()
            var nextToken: String? = null
            var marketplaceId: String? = null

            parser.findChildTagAsync(listOf("ListInventorySupplyResult", "ListInventorySupplyByNextTokenResult")) {
                parser.findChildTagAsync(listOf("InventorySupplyList", "NextToken", "MarketplaceId")) {
                    val name = parser.name
                    when (name) {
                        "InventorySupplyList" -> parser.findChildTagAsync("member") {
                            inventory.add(readInventory(parser))
                        }
                        "NextToken" -> nextToken = parser.readString(name)
                        "MarketplaceId" -> marketplaceId = parser.readString(name)
                    }
                }
            }

            return ListInventorySupply(
                inventory = inventory.filterNotNull(),
                marketplace = marketplaceFromId(marketplaceId),
                nextToken = nextToken
            )
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readInventory(parser: XmlPullParser): Inventory? {
        parser.require(XmlPullParser.START_TAG, ns, "member")

        var fnsku: String? = null
        var asin: String? = null
        var sku: String? = null
        var totalQuantity: Int? = null
        var condition: InventoryCondition? = null
        val quantity = arrayListOf<QuantityType?>()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name
            when (name) {
                "ASIN" -> asin = parser.readString(name)
                "SellerSKU" -> sku = parser.readString(name)
                "FNSKU" -> fnsku = parser.readString(name)
                "TotalSupplyQuantity" -> totalQuantity = parser.readString(name).toIntOrNull()
                "Condition" -> condition = try { InventoryCondition.valueOf(parser.readString(name)) } catch (e: Exception) { null }
                "SupplyDetail" -> {
                    parser.findChildTagAsync("member") {
                        quantity.add(readQuantity(parser))
                    }
                }
                else -> parser.skip()
            }
        }

        if (asin == null || condition == null || sku == null) { return null }


        return Inventory(
            fnsku = fnsku ?: "",
            asin = asin,
            sku = sku,
            condition = condition,
            quantity = Quantity(
                inbound = quantity.filter { it?.supplyType == SupplyType.Inbound }.sumBy { it?.quantity ?: 0 },
                instock = quantity.filter { it?.supplyType == SupplyType.InStock }.sumBy { it?.quantity ?: 0 },
                transfer = quantity.filter { it?.supplyType == SupplyType.Transfer }.sumBy { it?.quantity ?: 0 },
                total = totalQuantity ?: quantity.sumBy { it?.quantity ?: 0 }
            )
        )
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readQuantity(parser: XmlPullParser): QuantityType? {
        parser.require(XmlPullParser.START_TAG, ns, "member")

        var supplyType: SupplyType? = null
        var quantity: Int? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = parser.name
            when (name) {
                "SupplyType" -> supplyType = try { SupplyType.valueOf(parser.readString(name)) } catch (e: Exception) { null }
                "Quantity" -> quantity = parser.readString(name).toIntOrNull()
                else -> parser.skip()
            }
        }

        if (quantity == null || supplyType == null) { return null }

        return QuantityType(
            supplyType = supplyType,
            quantity = quantity
        )
    }

}