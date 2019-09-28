package com.theblissprogrammer.amazon.sdk.errors

import com.theblissprogrammer.amazon.sdk.extensions.scrubbed
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import okhttp3.Request
import org.json.JSONObject

/**
 * Created by ahmedsaad on 2017-11-09.
 * Copyright © 2017. All rights reserved.
 */

// Alias type used to represent specific field errors from the server
typealias FieldErrors = ArrayList<String>

/// The NetworkError type represents an error object returned from the API server.
class NetworkError(val urlRequest: Request? = null,
                   val statusCode: Int = 0,
                   val headerValues: Map<String, String> = mutableMapOf(),
                   var fieldErrors: FieldErrors = arrayListOf(),
                   val serverData: String? = null,
                   val internalError: Exception? = null) : Exception() {


    /// The initializer for the NetworkError type.
    ///
    /// - Parameters:
    ///   - statusCode: Status code of the network response.
    ///   - serverData: The data from the server that contains the error and corresponding fields.
    ///   - internalError: The internal error type from the network request.
    init {
        // Convert data to field error types
        this.fieldErrors = getFieldErrors(serverData)
    }

    private fun getFieldErrors(response: String?) : ArrayList<String> {
        val fieldErrors: FieldErrors = arrayListOf()

        try {
            response?.apply {
                val errors =  ErrorsXmlParser().parse(this)
                fieldErrors.addAll(errors)
            }
        } catch (e: Exception) {
            LogHelper.d(messages = *arrayOf("An error occurred while converting HTTP response " +
                    "${response ?: ""} to JSON: ${e.localizedMessage ?: ""}"))
        }

        return fieldErrors
    }

    val description: String
        get() {
            return "${internalError ?: DataError.UnknownReason(null)}\n" +
                    "        QueueRequest: {\n" +
                    "            url: ${urlRequest?.url()?.url()?.toString() ?: ""},\n" +
                    "            method: ${urlRequest?.method() ?: ""},\n" +
                    "            headers: ${urlRequest?.headers()?.toMultimap()?.scrubbed ?: ""},\n" +
                    "        },\n" +
                    "        Response: {\n" +
                    "            status: $statusCode,\n" +
                    "            body: $fieldErrors,\n" +
                    "            headers: ${headerValues.scrubbed}\n" +
                    "        }"
        }
}

