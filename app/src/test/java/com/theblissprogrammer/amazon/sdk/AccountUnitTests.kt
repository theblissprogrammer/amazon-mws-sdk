package com.theblissprogrammer.amazon.sdk

import androidx.test.core.app.ApplicationProvider
import com.theblissprogrammer.amazon.sdk.TestCredentials.Companion.mwsToken
import com.theblissprogrammer.amazon.sdk.TestCredentials.Companion.sellerID
import com.theblissprogrammer.amazon.sdk.account.AuthenticationWorkerType
import com.theblissprogrammer.amazon.sdk.account.models.LoginModels
import com.theblissprogrammer.amazon.sdk.dependencies.DependencyConfigurator
import com.theblissprogrammer.amazon.sdk.dependencies.HasDependencies
import com.theblissprogrammer.amazon.sdk.errors.DataError
import kotlinx.coroutines.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Created by ahmed.saad on 2018-12-03.
 * Copyright © 2018. All rights reserved.
 */
@RunWith(RobolectricTestRunner::class)
class AccountUnitTests: HasDependencies, DependencyConfigurator {

    private val authenticationWorker: AuthenticationWorkerType by lazy {
        dependencies.resolveAuthenticationWorker
    }

    @Before
    fun configure() {
        configure(application = ApplicationProvider.getApplicationContext(), dependencies = MockSDKDependency())
    }

    @Test
    fun `account login incomplete`() {
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
    fun `account login token incomplete`() {
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
    fun `account login invalid`() {
        val request = LoginModels.Request(
            sellerID = sellerID.drop(3),
            token = mwsToken
        )

        runBlocking {
            authenticationWorker.login(request) {
                Assert.assertTrue(
                    "Logging in with invalid values should fail.",
                    it.error is DataError.BadRequest
                )
            }
        }
    }

    @Test
    fun `account login valid`() {
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
    fun `account ping authorization`() {
        `account login valid`()

        runBlocking {
            authenticationWorker.pingAuthorization {
                Assert.assertTrue("An error occurred when there should not be: ${it.error?.localizedMessage ?: it.error}", it.isSuccess)
                Assert.assertNull("Login should return a null error.", it.error)
            }
        }
    }
}