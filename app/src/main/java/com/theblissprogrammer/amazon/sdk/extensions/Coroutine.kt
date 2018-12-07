package com.theblissprogrammer.amazon.sdk.extensions

import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.failure
import com.theblissprogrammer.amazon.sdk.common.CompletionResponse
import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.errors.DataError
import kotlinx.coroutines.*
import java.io.IOException

/**
 * Created by ahmedsaad on 2018-07-26.
 */
fun <T> coroutineCompletionOnUi (completion: CompletionResponse<T>? = null, call: suspend () -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        try {
            call()
        } catch (e: IOException){
            completion?.invoke(failure(
                    DataError.NetworkFailure(e)
            ))
        }
    }
}

fun <T> coroutineNetwork (call: () -> Result<T>): Deferred<Result<T>> {
    return GlobalScope.async(Dispatchers.IO) {
        call()
    }
}

fun <T> coroutineRoom (call: () -> LiveResult<T>): Deferred<LiveResult<T>> {
    return GlobalScope.async(Dispatchers.IO) {
        call()
    }
}

fun coroutineOnUi (call: suspend () -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        call()
    }
}

fun coroutine (call: suspend () -> Unit): Deferred<Unit> {
    return GlobalScope.async {
        call()
    }
}