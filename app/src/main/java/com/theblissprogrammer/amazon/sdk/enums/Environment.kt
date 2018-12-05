package com.theblissprogrammer.amazon.sdk.enums

import com.theblissprogrammer.amazon.sdk.BuildConfig

/**
 * Created by ahmedsaad on 2017-11-16.
 * Copyright © 2017. All rights reserved.
 */

enum class Environment {
    DEVELOPMENT,
    PRODUCTION;
    companion object {
        var mode: Environment = {
            if (BuildConfig.DEBUG)
                DEVELOPMENT
            else
                PRODUCTION
        }()
    }
}