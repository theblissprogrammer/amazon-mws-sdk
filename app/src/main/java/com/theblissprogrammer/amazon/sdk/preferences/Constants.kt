package com.theblissprogrammer.amazon.sdk.preferences

import java.util.*
import kotlin.experimental.xor

object NSObject
object NSString

/**
 * Created by ahmed.saad on 2018-12-05.
 * Copyright © 2018. All rights reserved.
 */
class Constants(val store: ConstantsStore): ConstantsType {
    override val developerAccountNumber: String
        get() = Companion.developerAccountNumber
    override val awsAccessKeyID: String
        get() = Companion.awsAccessKeyID
    override val awsSecretKey: String
        get() = Companion.awsSecretKey

    override val euDeveloperAccountNumber: String
        get() = Companion.euDeveloperAccountNumber
    override val euAwsAccessKeyID: String
        get() = Companion.euAwsAccessKeyID
    override val euAwsSecretKey: String
        get() = Companion.euAwsSecretKey

    override val advertisingClientID: String
        get() = Companion.advertisingClientID
    override val advertisingClientSecret: String
        get() = Companion.advertisingClientSecret
    override val advertisingRefreshToken: String
        get() = Companion.advertisingRefreshToken

    override val bannerAdUnitID: String
        get() = Companion.bannerAdUnitID


    /// Retrieves the constant from the store that corresponds to the given key.
    ///
    /// - Parameter key: The key that is used to read the store item.
    override fun <T> get(key: Int, default: T): T {
        return store.get(key, default)
    }

    companion object {
        /// The salt used to obfuscate and reveal the string.
        private val secretSalt = {
            Arrays.toString(arrayOf(
                NSObject::class.java.simpleName,
                    NSString::class.java.simpleName))
        }()

        /**
        This method obfuscates the string passed in using the salt
        that was used when the Obfuscator was initialized.
        https://gist.github.com/DejanEnspyra/80e259e3c9adf5e46632631b49cd1007

        - parameter string: the string to obfuscate

        - returns: the obfuscated string in a byte array
         **/
        /**fun obfuscate(string: String): String {
            val text = string.toByteArray(Charsets.UTF_8)
            val cipher = Constants.secretSalt.toByteArray(Charsets.UTF_8)
            val length = cipher.count()
            val encrypted = mutableListOf<Byte>()

            text.mapIndexedTo(encrypted) { index, value ->
                value xor cipher[index % length]
            }

            return encrypted.joinToString()
        }**/

        /**
        This method reveals the original string from the obfuscated
        byte array passed in. The salt must be the same as the one
        used to encrypt it in the first place.

        - parameter key: the byte array to reveal

        - returns: the original string
         */
        fun unobfuscate(key: ByteArray): String {
            val cipher = secretSalt.toByteArray(Charsets.UTF_8)
            val length = cipher.count()
            val decrypted = mutableListOf<Byte>()

            key.mapIndexedTo(decrypted) { index, value ->
                value xor cipher[index % length]
            }

            return String(decrypted.toByteArray(), charset = Charsets.UTF_8)
        }
    }
}