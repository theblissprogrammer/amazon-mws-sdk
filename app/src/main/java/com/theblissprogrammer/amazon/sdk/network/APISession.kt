package com.theblissprogrammer.amazon.sdk.network

import android.content.Context
import com.theblissprogrammer.amazon.sdk.common.NetworkResult
import com.theblissprogrammer.amazon.sdk.common.NetworkResult.Companion.failure
import com.theblissprogrammer.amazon.sdk.common.NoInternetException
import com.theblissprogrammer.amazon.sdk.common.isNetworkAvailable
import com.theblissprogrammer.amazon.sdk.enums.SecurityProperty.DEFAULT_TOKEN
import com.theblissprogrammer.amazon.sdk.errors.NetworkError
import com.theblissprogrammer.amazon.sdk.extensions.scrubbed
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.preferences.ConstantsType
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.security.SecurityWorkerType
import okhttp3.*
import java.util.concurrent.TimeUnit

/**
 * Created by ahmedsaad on 2017-11-03.
 * Copyright © 2017. All rights reserved.
 */
internal class APISession(private val context: Context?,
                          private val constants: ConstantsType,
                          private val securityWorker: SecurityWorkerType,
                          private val preferencesWorker: PreferencesWorkerType,
                          private val signedHelper: SignedHelperType
): APISessionType {

    private val sessionManager : OkHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS).build()

    /// Determine if user is authenticated with server
    override val isAuthorized: Boolean
        get() {
            // Assume user is logged in if they have a AUTH token.
            // However, future requests can return a 401-unauthorized,
            // which should log user out and redirect to login screen.
            return !securityWorker.get(DEFAULT_TOKEN).isNullOrBlank()
        }

    /// Remove stored tokens and credentials so requests will be unauthorized
    override fun unauthorize() {
        securityWorker.clear()
    }

    /// Creates a network request to retrieve the contents of a URL based on the specified router.
    ///
    /// - Parameters:
    ///   - router: The router request.
    ///   - completion: A handler to be called once the request has finished
    override fun request(router: APIRoutable): NetworkResult<ServerResponse> {
        // Validate connectivity
        if (!isNetworkAvailable(context = context)) {
            return failure(NetworkError(internalError = NoInternetException()))
        }

        val urlRequestBuilder: Request.Builder

        // Construct request
        try {
            urlRequestBuilder = router.asURLRequest(
                    constants = constants,
                    preferencesWorker = preferencesWorker,
                    securityWorker = securityWorker,
                    signedHelper = signedHelper)
        } catch (exception: Exception) {
            return failure(NetworkError(internalError = exception))
        }

        val urlRequest = urlRequestBuilder.build()

        // Log request/response debugging
        LogHelper.i(messages = *arrayOf("Request: $urlRequest"))

        val response = sessionManager.request(urlRequest)

        // Handle any pre-processing if applicable
        if (response.isSuccess && response.value != null) {
            LogHelper.d(messages = *arrayOf("Response: {\n\turl: ${urlRequest?.url()?.url()?.toString()}," +
                    "\n\tstatus: ${response.value.statusCode}, \n\theaders: ${response.value.headers.scrubbed}\n}"))
        } else if (response.error != null ){
            LogHelper.d(messages = *arrayOf("Network request error: ${response.error.description}"))
        }

        return response
    }
}