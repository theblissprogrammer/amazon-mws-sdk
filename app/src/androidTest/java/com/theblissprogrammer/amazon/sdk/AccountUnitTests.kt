package com.theblissprogrammer.amazon.sdk

import android.app.Application
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.theblissprogrammer.amazon.sdk.TestCredentials.Companion.mwsToken
import com.theblissprogrammer.amazon.sdk.TestCredentials.Companion.sellerID
import com.theblissprogrammer.amazon.sdk.account.AuthenticationWorkerType
import com.theblissprogrammer.amazon.sdk.account.models.LoginModels
import com.theblissprogrammer.amazon.sdk.access.MwsSdk
import com.theblissprogrammer.amazon.sdk.dependencies.HasDependencies
import com.theblissprogrammer.amazon.sdk.errors.DataError
import kotlinx.coroutines.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by ahmed.saad on 2018-12-03.
 * Copyright © 2018. All rights reserved.
 */
@RunWith(AndroidJUnit4::class)
class AccountUnitTests: HasDependencies {

    private val authenticationWorker: AuthenticationWorkerType by lazy {
        dependencies.resolveAuthenticationWorker
    }

    @Before
    fun configure() {
        MwsSdk.configure(
            application = InstrumentationRegistry.getTargetContext().applicationContext as Application,
            dependencies = MockSDKDependency()
        )
    }

    @Test
    fun account_login_incomplete() {
        val request = LoginModels.Request(
            sellerID = "",
            token = ""
        )

        runBlocking {
            authenticationWorker.login(request) {
                Assert.assertTrue(
                    "Logging in with empty values should fail.",
                    it.error is DataError.Incomplete
                )
            }
        }
    }

    @Test
    fun account_login_token_incomplete() {
        val request = LoginModels.Request(
            sellerID = sellerID,
            token = ""
        )

        runBlocking {
            authenticationWorker.login(request) {
                Assert.assertTrue(
                    "Logging in with empty values should fail.",
                    it.error is DataError.Incomplete
                )
            }
        }
    }

    @Test
    fun account_login_invalid() {
        val request = LoginModels.Request(
            sellerID = sellerID.drop(3),
            token = mwsToken
        )

        runBlocking {
            authenticationWorker.login(request) {
                Assert.assertFalse("Logging in with invalid values should fail.", it.isSuccess)
                Assert.assertNotNull(
                    "Logging in with invalid values should fail. ${it.error?.localizedMessage}",
                    it.error
                )
            }
        }
    }

    @Test
    fun account_login_valid() {
        val request = LoginModels.Request(
            sellerID = sellerID,
            token = mwsToken
        )

        runBlocking {
            authenticationWorker.login(request) {
                Assert.assertTrue("An error occurred when there should not be: ${it.error?.localizedMessage ?: it.error}", it.isSuccess)
                Assert.assertNull("Login should return a null error.", it.error)

                val seller = it.value?.seller
                Assert.assertNotNull("Login should return valid seller object.", seller)
                Assert.assertTrue("Seller returned does not equal seller used to sign in.", seller?.id?.equals(sellerID) == true)
            }
        }
    }

    @Test
    fun account_ping_authorization() {
        runBlocking {
            authenticationWorker.pingAuthorization {
                Assert.assertTrue("An error occurred when there should not be: ${it.error?.localizedMessage ?: it.error}", it.isSuccess)
                Assert.assertNull("Login should return a null error.", it.error)
            }
        }
    }
}