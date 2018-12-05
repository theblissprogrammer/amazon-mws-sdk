package com.theblissprogrammer.amazon.sdk.stores.performances.models

import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.PerformanceStatus

/**
 * Created by ahmedsaad on 2018-08-03.
 * Copyright (c) 2018. All rights reserved.
 **/
interface PerformanceType {
    var marketplaceType: MarketplaceType
    var defectStatus: PerformanceStatus
    var cancellationStatus: PerformanceStatus
    var lateShipmentStatus: PerformanceStatus
    var policyViolationStatus: PerformanceStatus
    var responseTimeStatus: PerformanceStatus
    var authenticityStatus: PerformanceStatus
    var safetyStatus: PerformanceStatus
    var listingPolicyStatus: PerformanceStatus
    var intellectualPropertyStatus: PerformanceStatus
}