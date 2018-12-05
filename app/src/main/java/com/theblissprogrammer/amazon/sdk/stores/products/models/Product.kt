package com.theblissprogrammer.amazon.sdk.stores.products.models

/**
 * Created by ahmedsaad on 2018-08-10.
 * Copyright (c) 2018. All rights reserved.
 **/
data class Product(
        override var asin: String = "",
        override var sku: String = "",
        override var name: String = "",
        override var description: String? = null,
        override var imageURL: String? = null,
        override var sellPrice: Double? = null,
        override var buyBoxPrice: Double? = null,
        override var fbaFees: Double? = null, 
        override var bsr: Int? = null,
        override var category: String? = null,
        override var sellers: Int? = null,
        override var isFBA: Boolean = false,
        override var isActive: Boolean = false,
        override var merchantQuantity: Int? = null) : ProductType {

    constructor(from: ProductType?): this() {
        from?.let { product ->
            this.asin = product.asin
            this.sku = product.sku
            this.name = product.name
            this.description = product.description
            this.imageURL = product.imageURL
            this.sellPrice = product.sellPrice
            this.buyBoxPrice = product.buyBoxPrice
            this.fbaFees = product.fbaFees
            this.bsr = product.bsr
            this.category = product.category
            this.sellers = product.sellers
            this.isFBA = product.isFBA
            this.isActive = product.isActive
            this.merchantQuantity = product.merchantQuantity
        }
    }
}