package com.theblissprogrammer.amazon.sdk.network

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.extensions.coroutineBackgroundAsync
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetworkAsync
import com.theblissprogrammer.amazon.sdk.extensions.coroutineOnIO
import com.theblissprogrammer.amazon.sdk.extensions.coroutineOnUi

/**
 * Created by ahmed.saad on 2019-09-27.
 * Copyright © 2019. All rights reserved.
 */
abstract class NetworkBoundResource<ResultType, RequestType>
@MainThread constructor() {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        @Suppress("LeakingThis")

        coroutineOnIO {
            val dbSource = loadFromDb()

            val value = dbSource.value ?: MutableLiveData()

            coroutineOnUi {
                result.addSource(value) { data ->
                    result.removeSource(value)

                    if (shouldFetch(data)) {
                        fetchFromNetwork(value)
                    } else {
                        result.addSource(value) { newData ->
                            setValue(Resource.success(newData))
                        }
                    }
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val call = coroutineNetworkAsync {
            createCall()
        }
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newData ->
            setValue(Resource.loading(newData))
        }

        coroutineOnIO {
            val apiResonse = call.await()

            coroutineOnUi {
                result.removeSource(dbSource)
            }

            if (apiResonse.error != null) {
                onFetchFailed()

                coroutineOnUi {
                    result.addSource(dbSource) { newData ->
                        setValue(Resource.error(apiResonse.error, newData))
                    }
                }

                return@coroutineOnIO
            }

            // we specially request a new live data,
            // otherwise we will get immediately last cached value,
            // which may not be updated with latest results received from network.
            val newdbSource = loadFromDb()
            val value = newdbSource.value ?: MutableLiveData()

            if (apiResonse.value == null) {
                coroutineOnUi {
                    result.addSource(value) { newData ->
                        setValue(Resource.success(newData))
                    }
                }

                return@coroutineOnIO
            }

            coroutineBackgroundAsync {
                saveCallResult(processResponse(apiResonse))
            }.await()

            coroutineOnUi {
                result.addSource(value) { newData ->
                    setValue(Resource.success(newData))
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected open fun processResponse(response: Result<RequestType>) = response.value

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType?)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveResult<ResultType>

    @MainThread
    protected abstract fun createCall(): Result<RequestType>
}