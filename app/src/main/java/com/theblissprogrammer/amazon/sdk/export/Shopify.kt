package com.theblissprogrammer.amazon.sdk.export

import android.os.Environment
import com.theblissprogrammer.amazon.sdk.extensions.makeCSVCompatible
import com.theblissprogrammer.amazon.sdk.stores.details.models.ProductDetail
import com.theblissprogrammer.amazon.sdk.stores.products.models.Product
import org.jsoup.Jsoup
import java.io.File
import java.io.FileWriter


/**
 * Created by ahmed.saad on 2019-03-04.
 * Copyright © 2019. All rights reserved.
 */
 class Shopify {

    companion object {
        private val CSV_HEADER =
            "Handle,Title,Body (HTML),Vendor,Type,Tags,Published,Option1 Name,Option1 Value,Option2 Name," +
                    "Option2 Value,Option3 Name,Option3 Value,Variant SKU,Variant Grams,Variant Inventory Tracker," +
                    "Variant Inventory Qty,Variant Inventory Policy,Variant Fulfillment Service,Variant Price," +
                    "Variant Compare At Price,Variant Requires Shipping,Variant Taxable,Variant Barcode,Image Src,Image Position," +
                    "Image Alt Text,Gift Card,Google Shopping / MPN,Google Shopping / Age Group,Google Shopping / Gender," +
                    "Google Shopping / Google Product Category,SEO Title,SEO Description,Google Shopping / AdWords Grouping," +
                    "Google Shopping / AdWords Labels,Google Shopping / Condition,Google Shopping / Custom Product," +
                    "Google Shopping / Custom Label 0,Google Shopping / Custom Label 1,Google Shopping / Custom Label 2," +
                    "Google Shopping / Custom Label 3,Google Shopping / Custom Label 4,Variant Image,Variant Weight Unit," +
                    "Variant Tax Code,Cost per item"

        fun export(products: List<ProductDetail>) {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "shopify_product_export.csv")

            FileWriter(file, false).use { writer ->
                writer.append(CSV_HEADER)
                writer.append('\n')

                products.forEach {
                    writer.append(make(it).toString())
                    writer.append('\n')

                    if (it.detail.images.size > 1) {
                        it.detail.images.forEachIndexed { index, s ->
                            if (index == 0) return@forEachIndexed

                            writer.append(
                                ShopifyProduct(
                                    Handle = it.detail.asin,
                                    ImageSrc = s
                                ).toString()
                            )

                            writer.append('\n')
                        }
                    }
                }

                writer.flush()
            }
        }

        private fun make(productDetail: ProductDetail): ShopifyProduct {
            val product = productDetail.products.first()

            val title = (if (productDetail.detail.title != "Robot Check") productDetail.detail.title else
                product.name).makeCSVCompatible()

            val description =  Jsoup.parse((productDetail.detail.features ?: "") + " " + (productDetail.detail.description ?: ""))
                .text().makeCSVCompatible()

            return ShopifyProduct(
                Handle = productDetail.detail.asin,
                Title = title,
                Body = description,
                Vendor = productDetail.detail.manufacturer?.makeCSVCompatible() ?: "",
                Type = productDetail.detail.category?.makeCSVCompatible() ?: product.category ?: "",
                VariantSKU = product.sku,
                VariantGrams = productDetail.detail.weight ?: "",
                VariantInventoryQty = "",
                VariantPrice = product.sellPrice.toString(),
                VariantCompareAtPrice = "",
                VariantBarcode = productDetail.detail.manufacturerReference?.makeCSVCompatible() ?: product.asin,
                ImageSrc = productDetail.detail.images[0],
                SEOTitle = title,
                SEODescription = description,
                Published = "TRUE",
                Option1Name = "Title",
                Option1Value = "Default Title",
                VariantInventoryTracker = "amazon_marketplace_web",
                VariantInventoryPolicy = "deny",
                VariantFulfillmentService = "amazon_marketplace_web",
                VariantRequiresShipping = "TRUE",
                VariantTaxable = "TRUE",
                GiftCard = "FALSE"

            )
        }
    }
}