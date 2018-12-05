package com.theblissprogrammer.amazon.sdk.security

import android.content.Context
import android.os.Build
import java.security.*
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.math.BigInteger
import java.util.*
import javax.security.auth.x500.X500Principal
import java.io.ByteArrayOutputStream
import javax.crypto.*
import android.util.Base64
import com.theblissprogrammer.amazon.sdk.enums.SecurityProperty
import com.theblissprogrammer.amazon.sdk.enums.SecurityProperty.*
import java.io.ByteArrayInputStream

/**
 * Created by ahmedsaad on 2017-11-10.
 * Copyright © 2017. All rights reserved.
 */

class SecurityWorker(val context: Context?,
                     val store: SecurityStore
) : SecurityWorkerType {

    companion object {
        private val ALIAS = "USER_ALIAS"
        private val ANDROID_KEY_STORE = "AndroidKeyStore"
        private val TRANSFORMATION = "RSA/ECB/PKCS1Padding"
        private var TokenCached: Pair<TOKEN, String?>? = null
        private var DefaultTokenCached: String? = null
    }

    private val keyStore: KeyStore by lazy {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
         keyStore
    }

    /// Retrieves the text value from the keychain that corresponds to the given key.
    ///
    /// - Parameter key: The key that is used to read the preference item.
    override fun get(key: SecurityProperty): String? {
        val tokenCached = TokenCached

        if (key is TOKEN && tokenCached != null && key.region == tokenCached.first.region) {
            return tokenCached.second
        }

        if (DefaultTokenCached != null && key === DEFAULT_TOKEN) {
            return DefaultTokenCached
        }

        val value = decrypt(store.get(key))

        if (key is TOKEN) {
            TokenCached = Pair(key, value)
        }

        if (key === DEFAULT_TOKEN) {
            DefaultTokenCached = value
        }

        return value
    }

    /// Stores the text value in the keychain item under the given key.
    ///
    /// - Parameters:
    ///   - value: Text string to be written to the preference.
    ///   - key: Key under which the text value is stored in the preference.
    /// - Returns: True if the item was successfully set.
    override fun set(key: SecurityProperty, value: String?): Boolean  {
        val encrypted = encrypt(value)

        if (key is TOKEN) {
            TokenCached = Pair(key, value)

            // Set Default token to let apisession know user is logged in
            DefaultTokenCached = value
            store.set(DEFAULT_TOKEN, value = encrypted)
        }

        return store.set(key, value = encrypted)
    }

    /// Deletes the single keychain item specified by the key.
    ///
    /// - Parameter key: The key that is used to delete the preference item.
    /// - Returns: True if the item was successfully deleted.
    override fun delete(key: SecurityProperty): Boolean {
        if (key is TOKEN) {
            TokenCached = null
        }

        return store.delete(key)
    }

    /// Removes all the preference items.
    override fun clear() {
        TokenCached = null
        store.clear()
    }

    override fun createKey(alias: String) {
        try {
            // Create new key if needed
            if (!keyStore.containsAlias(alias)) {
                val spec = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    KeyGenParameterSpec.Builder(alias,
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                            .build()
                } else if (context != null) {
                    val start = Calendar.getInstance()
                    val end = Calendar.getInstance()
                    end.add(Calendar.YEAR, 1)
                    KeyPairGeneratorSpec.Builder(context)
                            .setAlias(alias)
                            .setSubject(X500Principal("CN=User, O=Example Inc"))
                            .setSerialNumber(BigInteger.ONE)
                            .setStartDate(start.time)
                            .setEndDate(end.time)
                            .build()
                } else {
                    null
                }

                val generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore")
                generator.initialize(spec)

                generator.generateKeyPair()
            } else {
                deleteKey(alias)
                createKey(alias)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun deleteKey(alias: String) {
        try {
            keyStore.deleteEntry(alias)
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        }
    }

    override fun encrypt(value: String?): String? {
        if (value == null) {
            return null
        }

        if (!keyStore.containsAlias(ALIAS)) {
            createKey(ALIAS)
        }

        try {
            val privateKeyEntry = keyStore.getEntry(ALIAS, null) as? KeyStore.PrivateKeyEntry
            val publicKey = privateKeyEntry?.certificate?.publicKey

            // Encrypt the text
            if (value.isEmpty()) {
                return null
            }

            val input = Cipher.getInstance(TRANSFORMATION)
            input.init(Cipher.ENCRYPT_MODE, publicKey)

            val outputStream = ByteArrayOutputStream()
            val cipherOutputStream = CipherOutputStream(
                    outputStream, input)

            cipherOutputStream.use { s ->
                s.write(value.toByteArray(charset("UTF-8")))
            }

            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null

    }

    override fun decrypt(value: String?): String? {
        if (value == null) {
            return null
        }

        if (!keyStore.containsAlias(ALIAS)) {
            createKey(ALIAS)
        }

        try {
            val privateKeyEntry = keyStore.getEntry(ALIAS, null) as? KeyStore.PrivateKeyEntry
            val privateKey = privateKeyEntry?.privateKey

            val output = Cipher.getInstance(TRANSFORMATION)
            output.init(Cipher.DECRYPT_MODE, privateKey)

            val cipherInputStream = CipherInputStream(
                    ByteArrayInputStream(Base64.decode(value, Base64.DEFAULT)), output)

            var decryptedString: String? = null
            cipherInputStream.use { r ->
                r.reader().forEachLine {
                    decryptedString = it
                }
            }

            return decryptedString
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}