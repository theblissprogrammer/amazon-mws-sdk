package com.theblissprogrammer.amazon.sdk.stores.fbaFees.models

/**
 * Created by ahmedsaad on 2018-08-27.
 * Copyright (c) 2018. All rights reserved.
 **/
data class FBAFee(
        override var sku: String = "",
        override var asin: String = "",
        override var longestSide: Double? = null,
        override var medianSide: Double? = null,
        override var shortestSide: Double? = null,
        override var lengthGirth: Double? = null,
        override var unitDimension: String? = null,
        override var weight: Double? = null,
        override var unitWeight: String? = null,
        override var productSizeTier: String? = null,
        override var currency: String? = null,
        override var feeTotal: Double? = null,
        override var referralFee: Double? = null,
        override var pickPackFee: Double? = null,
        override var percentageOfPrice: Double? = null): FBAFeeType {

    constructor(from: FBAFeeType?): this() {
        from?.let { fbaFee ->
            this.sku = fbaFee.sku
            this.asin = fbaFee.asin
            this.longestSide = fbaFee.longestSide
            this.medianSide = fbaFee.medianSide
            this.shortestSide = fbaFee.shortestSide
            this.lengthGirth = fbaFee.lengthGirth
            this.unitDimension = fbaFee.unitDimension
            this.weight = fbaFee.weight
            this.unitWeight = fbaFee.unitWeight
            this.productSizeTier = fbaFee.productSizeTier
            this.currency = fbaFee.currency
            this.feeTotal = fbaFee.feeTotal
            this.referralFee = fbaFee.referralFee
            this.pickPackFee = fbaFee.pickPackFee
            this.percentageOfPrice = fbaFee.percentageOfPrice
        }
    }
}