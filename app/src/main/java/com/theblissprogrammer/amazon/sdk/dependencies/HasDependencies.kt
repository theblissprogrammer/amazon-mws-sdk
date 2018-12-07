package com.theblissprogrammer.amazon.sdk.dependencies


/**
 * Created by ahmedsaad on 2017-11-30.
 * Copyright © 2017. All rights reserved.
 */
internal interface HasDependencies {
    /// Container for dependency instance factories
    val dependencies: SDKDependable
        get() {
            return DependencyInjector.dependencies
        }
}

/// Used to pass around dependency container
/// which can be reassigned to another container
internal class DependencyInjector {
    companion object {
        lateinit var dependencies: SDKDependable
    }
}