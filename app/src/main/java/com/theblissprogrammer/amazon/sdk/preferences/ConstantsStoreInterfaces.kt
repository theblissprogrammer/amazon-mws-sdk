package com.theblissprogrammer.amazon.sdk.preferences

import com.theblissprogrammer.amazon.sdk.R
import com.theblissprogrammer.amazon.sdk.enums.Environment

interface ConstantsStore {
    fun <T> get(key: Int, default: T): T
}

interface ConstantsType: ConstantsStore {
    /// NA MWS Keys
    val developerAccountNumber: String
    val awsAccessKeyID: String
    val awsSecretKey: String

    /// EU MWS Keys
    val euDeveloperAccountNumber: String
    val euAwsAccessKeyID: String
    val euAwsSecretKey: String

    /// Advertising Keys
    val advertisingClientID: String
    val advertisingClientSecret: String
    val advertisingRefreshToken: String

    /// Admob
    val bannerAdUnitID: String

    /// SQS AWS
    val sqsAwsAccessKeyID: String
    val sqsAwsSecretKey: String


    /// Email of admin used for users
    val emailUserAdmin: String
        get() {
            return when (Environment.mode) {
                Environment.DEVELOPMENT -> get(R.string.email_user_admin_debug, "")
                else -> get(R.string.email_user_admin, "")
            }
        }
}