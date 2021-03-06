package com.theblissprogrammer.amazon.sdk.export

/**
 * Created by ahmed.saad on 2019-03-04.
 * Copyright © 2019. All rights reserved.
 */
 data class ShopifyProduct(
    val Handle: String,
    val Title: String = "",
    val Body: String? = null,
    val Vendor: String = "",
    val Type: String? = null,
    val Tags: String = "",
    val Published: String = "",
    val Option1Name: String = "",
    val Option1Value: String = "",
    val Option2Name: String = "",
    val Option2Value: String = "",
    val Option3Name: String = "",
    val Option3Value: String = "",
    val VariantSKU: String = "",
    val VariantGrams: String? = null,
    // shopify, shipwire, amazon_marketplace_web, or blank
    val VariantInventoryTracker: String = "",
    val VariantInventoryQty: String = "",
    // deny, or continue
    val VariantInventoryPolicy: String = "",
    // manual, shipwire, webgistix, amazon_marketplace_web
    val VariantFulfillmentService: String = "",
    val VariantPrice: String = "",
    val VariantCompareAtPrice: String = "",
    val VariantRequiresShipping: String = "",
    val VariantTaxable: String = "",
    val VariantBarcode: String = "",
    val ImageSrc: String = "",
    val ImagePosition: String = "",
    val ImageAltText: String = "",
    val GiftCard: String = "",
    val GoogleShoppingMPN: String = "",
    val GoogleShoppingAgeGroup: String = "",
    val GoogleShoppingGender: String = "",
    val GoogleShoppingGoogleProductCategory: String = "",
    val SEOTitle: String = "",
    val SEODescription: String? = null,
    val GoogleShoppingAdWordsGrouping: String = "",
    val GoogleShoppingAdWordsLabels: String = "",
    val GoogleShoppingCondition: String = "",
    val GoogleShoppingCustomProduct: String = "",
    val GoogleShoppingCustomLabel0: String = "",
    val GoogleShoppingCustomLabel1: String = "",
    val GoogleShoppingCustomLabel2: String = "",
    val GoogleShoppingCustomLabel3: String = "",
    val GoogleShoppingCustomLabel4: String = "",
    val VariantImage: String = "",
    // lb, kg, and oz
    val VariantWeightUnit: String = "",
    val VariantTaxCode: String = "",
    val CostPerItem: String = "") {

    override fun toString(): String {
        return "$Handle,$Title,${Body ?: ""},$Vendor,${Type ?: ""},$Tags,$Published,$Option1Name,$Option1Value,$Option2Name," +
                "$Option2Value,$Option3Name,$Option3Value,$VariantSKU,${VariantGrams ?: ""},$VariantInventoryTracker," +
                "$VariantInventoryQty,$VariantInventoryPolicy,$VariantFulfillmentService,$VariantPrice," +
                "$VariantCompareAtPrice,$VariantRequiresShipping,$VariantTaxable,$VariantBarcode,$ImageSrc,$ImagePosition," +
                "$ImageAltText,$GiftCard,$GoogleShoppingMPN,$GoogleShoppingAgeGroup,$GoogleShoppingGender," +
                "$GoogleShoppingGoogleProductCategory,$SEOTitle,${SEODescription ?: ""},$GoogleShoppingAdWordsGrouping," +
                "$GoogleShoppingAdWordsLabels,$GoogleShoppingCondition,$GoogleShoppingCustomProduct," +
                "$GoogleShoppingCustomLabel0,$GoogleShoppingCustomLabel1,$GoogleShoppingCustomLabel2," +
                "$GoogleShoppingCustomLabel3,$GoogleShoppingCustomLabel4,$VariantImage,$VariantWeightUnit," +
                "$VariantTaxCode,$CostPerItem"
    }
}