package com.theblissprogrammer.amazon.sdk.access

import android.app.Application
import com.theblissprogrammer.amazon.sdk.dependencies.DependencyInjector
import com.theblissprogrammer.amazon.sdk.dependencies.SDKDependable
import com.theblissprogrammer.amazon.sdk.dependencies.SDKDependency

/**
 * Created by ahmed.saad on 2018-12-06.
 * Copyright © 2018. All rights reserved.
 */
class MwsSdk {
    companion object {
        fun configure(application: Application, dependencies: SDKDependable = SDKDependency()) {
            dependencies.application = application
            DependencyInjector.dependencies = dependencies
        }

        val dataManager: DataManagerInterface by lazy {
            DataManager()
        }
    }
}