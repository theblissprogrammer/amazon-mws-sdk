package com.theblissprogrammer.amazon.sdk.common

import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.errors.NetworkError

/**
 * Created by ahmedsaad on 2018-01-03.
 * Copyright © 2017. All rights reserved.
 */

class NoInternetException: Exception()

fun initDataError(error: NetworkError?): DataError {
    // Handle no internet
    if (error?.internalError is NoInternetException) {
        return DataError.NoInternet
    }

    // Handle by status code
    return when (error?.statusCode) {
        400 -> DataError.NetworkFailure(error)
        401 -> DataError.Unauthorized
        403 -> DataError.Forbidden
        else -> DataError.Other(error?.fieldErrors ?: mutableMapOf())
    }
}