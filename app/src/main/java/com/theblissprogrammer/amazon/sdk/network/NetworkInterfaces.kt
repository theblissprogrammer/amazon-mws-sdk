package com.theblissprogrammer.amazon.sdk.network

import com.theblissprogrammer.amazon.sdk.common.NetworkResult
import com.theblissprogrammer.amazon.sdk.enums.NetworkMethod
import com.theblissprogrammer.amazon.sdk.preferences.ConstantsType
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.security.SecurityWorkerType
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ahmed.saad on 2018-12-03.
 * Copyright © 2018. All rights reserved.
 */
interface SignedHelperType {
    fun hmac(stringToSign: String): String
}

interface HTTPServiceType {
    fun post(url: String, parameters: Map<String, Any?>? = null, body: String? = "",
             headers: Map<String, String>? = null): NetworkResult<ServerResponse>
    fun get(url: String, parameters: Map<String, Any?>? = null, headers: Map<String, String>? = null)
            : NetworkResult<ServerResponse>
}

interface APISessionType {
    val isAuthorized: Boolean
    fun unauthorize()
    fun request(router: APIRoutable): NetworkResult<ServerResponse>
}

abstract class APIRoutable {
    companion object {
        val JSON = MediaType.parse("application/json; charset=utf-8")
    }

    open val method: NetworkMethod = NetworkMethod.POST
    abstract val path: String
    open val requestBody: RequestBody? = null
    open val queryParameterList: MutableMap<String, String> = mutableMapOf()

    /**
     * Generate a ISO-8601 format timestamp as required by Amazon.
     *
     * @return  ISO-8601 format timestamp.
     */
    val timestamp: String
        get() {
            val timestamp: String
            val cal = Calendar.getInstance()
            val dfm = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            dfm.timeZone = TimeZone.getTimeZone("GMT")
            timestamp = dfm.format(cal.time)
            return timestamp
        }

    abstract fun getURL(constants: ConstantsType,
                        preferencesWorker: PreferencesWorkerType,
                        securityWorker: SecurityWorkerType,
                        signedHelper: SignedHelperType
    ): String

    fun asURLRequest(constants: ConstantsType,
                     preferencesWorker: PreferencesWorkerType,
                     securityWorker: SecurityWorkerType,
                     signedHelper: SignedHelperType
    ) : Request.Builder {


        val url = getURL(constants, preferencesWorker, securityWorker, signedHelper)

        val requestBody =  requestBody ?: if (method == NetworkMethod.GET || method == NetworkMethod.DELETE)
            null
        else if (requestBody != null)
            requestBody
        else
            RequestBody.create(null, "")

        return Request.Builder()
            .url(url)
            .method(method.name, requestBody)
    }
}