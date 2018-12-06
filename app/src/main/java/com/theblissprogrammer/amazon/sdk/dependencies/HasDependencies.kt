package com.theblissprogrammer.amazon.sdk.dependencies

import android.app.Application


/**
 * Created by ahmedsaad on 2017-11-30.
 * Copyright © 2017. All rights reserved.
 */
interface HasDependencies {
    /// Container for dependency instance factories
    val dependencies: SDKDependable
        get() {
            return DependencyInjector.dependencies as SDKDependable
        }
}

class MwsSdk {
    companion object {
        fun configure(application: Application, dependencies: SDKDependable = SDKDependency()) {
            dependencies.application = application
            DependencyInjector.dependencies = dependencies
        }
    }
}

/// Used to pass around dependency container
/// which can be reassigned to another container
internal class DependencyInjector {
    companion object {
        lateinit var dependencies: CoreDependable
    }
}