package com.theblissprogrammer.amazon.sdk.dependencies

import android.app.Application


/**
 * Created by ahmedsaad on 2017-11-30.
 * Copyright © 2017. All rights reserved.
 */
 internal interface HasDependencies {

    /// Container for dependency instance factories
    val dependencies: SDKDependable
        get() {
            return DependencyInjector.dependencies as SDKDependable
        }
}

interface DependencyConfigurator {

    /// Declare dependency container to use
    fun configure(dependencies: CoreDependable) {
        DependencyInjector.dependencies = dependencies
    }

    fun configure(application: Application, dependencies: SDKDependable) {
        dependencies.application = application

        configure(dependencies as CoreDependable)
        DependencyInjector.dependencies = dependencies
    }
}

/// Used to pass around dependency container
/// which can be reassigned to another container
class DependencyInjector {
    companion object {
        var dependencies: CoreDependable = SDKDependency()
    }
}