package com.theblissprogrammer.amazon.sdk.stores.fbaFees.models

/**
 * Created by ahmedsaad on 2018-08-27.
 * Copyright (c) 2018. All rights reserved.
 **/
interface FBAFeeType {
    var sku: String
    var asin: String
    var longestSide: Double?
    var medianSide: Double?
    var shortestSide: Double?
    var lengthGirth: Double?
    var unitDimension: String?
    var weight: Double?
    var unitWeight: String?
    var productSizeTier: String?
    var currency: String?
    var feeTotal: Double?
    var referralFee: Double?
    var pickPackFee: Double?
    var percentageOfPrice: Double?
}