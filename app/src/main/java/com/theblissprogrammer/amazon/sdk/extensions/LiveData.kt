package com.theblissprogrammer.amazon.sdk.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

/**
 * Created by ahmed.saad on 2019-09-23.
 * Copyright © 2019. All rights reserved.
 */
fun <T, Y> LiveData<T>.switchMap(call: (T?) -> Y?): LiveData<Y> {
    return Transformations.map(this, call)
}