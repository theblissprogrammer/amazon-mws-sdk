package com.theblissprogrammer.amazon.sdk

import com.theblissprogrammer.amazon.sdk.dependencies.SDKDependency
import com.theblissprogrammer.amazon.sdk.security.SecurityWorkerType

/**
 * Created by ahmed.saad on 2018-12-03.
 * Copyright © 2018. All rights reserved.
 */
class MockSDKDependency: SDKDependency() {
    override val resolveSecurityWorker: SecurityWorkerType by lazy {
        MockSecurityWorker(resolveSecurityStore)
    }
}