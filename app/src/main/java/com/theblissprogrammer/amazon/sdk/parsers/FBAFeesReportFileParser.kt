package com.theblissprogrammer.amazon.sdk.parsers

import com.theblissprogrammer.amazon.sdk.stores.fbaFees.models.FBAFee


/**
 * Created by ahmedsaad on 2018-02-20.
 * Copyright © 2017. All rights reserved.
 */
class FBAFeesReportFileParser {

    fun parse(input: String): List<FBAFee> {
        val products = input.split(Regex("[\r\n]+")).filter { it.isNotEmpty() }
                .map { it.split("\t") }
        val header = products.firstOrNull { it.contains("sku")} ?: return listOf()

        val skuIndex = header.indexOf("sku")
        val asinIndex = header.indexOf("asin")
        val longestSideIndex = header.indexOf("longest-side")
        val medianSideIndex = header.indexOf("median-side")
        val shortestSideIndex = header.indexOf("shortest-side")
        val lengthIndex = header.indexOf("length-and-girth")
        val unitDimensionIndex = header.indexOf("unit-of-dimension")
        val weightIndex = header.indexOf("item-package-weight")
        val weightDimensionIndex = header.indexOf("unit-of-weight")
        val tierIndex = header.indexOf("product-size-tier")
        val currencyIndex = header.indexOf("currency")
        val priceIndex = header.indexOf("sales-price")
        val feeIndex = header.indexOf("estimated-fee-total")
        val referralFeeIndex = header.indexOf("estimated-referral-fee-per-unit")
        val pickPackIndex = header.indexOf("expected-fulfillment-fee-per-unit")

        return products.filter { !it.contains("sku") && it.isNotEmpty() }.map {
            FBAFee(
                    sku = it[skuIndex],
                    asin = it[asinIndex],
                    longestSide = it[longestSideIndex].toDoubleOrNull(),
                    medianSide = it[medianSideIndex].toDoubleOrNull(),
                    shortestSide = it[shortestSideIndex].toDoubleOrNull(),
                    lengthGirth = it[lengthIndex].toDoubleOrNull(),
                    unitDimension = it[unitDimensionIndex],
                    weight = it[weightIndex].toDoubleOrNull(),
                    unitWeight = it[weightDimensionIndex],
                    productSizeTier = it[tierIndex],
                    currency = it[currencyIndex],
                    feeTotal = it[feeIndex].toDoubleOrNull(),
                    referralFee = it[referralFeeIndex].toDoubleOrNull(),
                    pickPackFee = it[pickPackIndex].toDoubleOrNull(),
                    percentageOfPrice = (it[referralFeeIndex].toDoubleOrNull() ?: 0.0) / (it[priceIndex].toDoubleOrNull() ?: 1.0)
            )
        }
    }
}