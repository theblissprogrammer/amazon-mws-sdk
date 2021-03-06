package com.theblissprogrammer.amazon.sdk.parsers

import com.theblissprogrammer.amazon.sdk.stores.products.models.Product


/**
 * Created by ahmedsaad on 2018-02-20.
 * Copyright © 2017. All rights reserved.
 */

/**
 * This parser will parse the Inventory Reports from the report api
 * The columns available are as follows:
 *
 * item-name
 * item-description
 * listing-id
 * seller-sku
 * price
 * quantity
 * open-date
 * image-url
 * item-is-marketplace
 * product-id-type
 * zshop-shipping-fee
 * item-note
 * item-condition
 * zshop-category1
 * zshop-browse-path
 * zshop-storefront-feature
 * asin1
 * asin2
 * asin3
 * will-ship-internationally
 * expedited-shipping
 * zshop-boldface
 * product-id
 * bid-for-featured-placement
 * add-delete
 * pending-quantity
 * fulfillment-channel
 * merchant-shipping-group
 * status
 **/
class ProductsReportFileParser {

    fun parse(input: String): List<Product> {
        val products = input.split(Regex("[\r\n]+")).filter { it.isNotEmpty() }
                .map { it.split("\t") }
        val header = products.firstOrNull { it.contains("seller-sku") } ?: return listOf()

        val skuIndex = header.indexOf("seller-sku")
        val asinIndex = header.indexOf("asin1")
        val nameIndex = header.indexOf("item-name")
        val priceIndex = header.indexOf("price")
        val quantityIndex = header.indexOf("quantity")
        val fbaIndex = header.indexOf("fulfillment-channel")
        val statusIndex = header.indexOf("status")
        val imageIndex = header.indexOf("image-url")
        val descriptionIndex = header.indexOf("item-description")

        return products.filter { !it.contains("seller-sku") && it.isNotEmpty() }.map {
            Product(
                    sku = it[skuIndex],
                    asin = it[asinIndex],
                    name = it[nameIndex],
                    description = it[descriptionIndex],
                    imageURL = it[imageIndex],
                    sellPrice = it[priceIndex].toDoubleOrNull(),
                    isFBA = it[fbaIndex].contains("amazon", ignoreCase = true),
                    isActive = it[statusIndex].equals("Active", ignoreCase = true)
            )
        }
    }
}