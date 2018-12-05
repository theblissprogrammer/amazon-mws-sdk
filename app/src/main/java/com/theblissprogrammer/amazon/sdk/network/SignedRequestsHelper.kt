package com.theblissprogrammer.amazon.sdk.network

import org.apache.commons.codec.binary.Base64
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


/**
 * Created by ahmedsaad on 2018-02-08.
 * Copyright © 2017. All rights reserved.
 */
class SignedRequestsHelper(val secretKey: String): SignedHelperType {

    companion object {
        const val HMAC_SHA256_ALGORITHM = "HmacSHA256"

        /**
         * Percent-encode values according the RFC 3986. The built-in Java
         * URLEncoder does not encode according to the RFC, so we make the
         * extra replacements.
         *
         * @param s decoded string
         * @return  encoded string per RFC 3986
         */
        fun percentEncodeRfc3986(s: String): String {
            val out = try {
                URLEncoder.encode(s, "UTF-8")
                        .replace("+", "%20")
                        .replace("*", "%2A")
                        .replace("%7E", "~")
            } catch (e: UnsupportedEncodingException) {
                s
            }

            return out
        }


        /**
         * Canonicalize the query string as required by Amazon.
         *
         * @return   Canonical form of query string.
         */
        fun canonicalize(params: Map<String, String>): String {
            val sortedParamMap = TreeMap<String, String>(params)

            if (sortedParamMap.isEmpty()) {
                return ""
            }

            val buffer = StringBuilder()

            sortedParamMap.entries.forEachIndexed { index, entry ->
                buffer.append(percentEncodeRfc3986(entry.key))
                buffer.append("=")
                buffer.append(percentEncodeRfc3986(entry.value))

                if (entry.key != sortedParamMap.lastKey()) {
                    buffer.append("&")
                }
            }

            return buffer.toString()
        }
    }

    private val secretKeySpec: SecretKeySpec by lazy {
        SecretKeySpec(
                secretKey.toByteArray(Charsets.UTF_8),
                HMAC_SHA256_ALGORITHM
        )
    }

    private val mac: Mac by lazy {
        Mac.getInstance(HMAC_SHA256_ALGORITHM)
    }

    init {
        mac.init(secretKeySpec)
    }

    /**
     * Compute the HMAC.
     *
     * @param stringToSign  String to compute the HMAC over.
     * @return              base64-encoded hmac value.
     */
    override fun hmac(stringToSign: String): String {
        val signature: String
        val data: ByteArray
        val rawHmac: ByteArray
        try {
            data = stringToSign.toByteArray(Charsets.UTF_8)
            rawHmac = mac.doFinal(data)
            signature = String(Base64.encodeBase64(rawHmac))
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException("UTF_8 is unsupported!", e)
        }

        return percentEncodeRfc3986(signature)
    }
}