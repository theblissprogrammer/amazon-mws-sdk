package com.theblissprogrammer.amazon.sdk.errors

/**
 * Created by ahmedsaad on 2017-10-24.
 */

sealed class DataError : Exception() {
    object DuplicateFailure : DataError()
    object NonExistent : DataError()
    object Incomplete : DataError()
    object Unauthorized : DataError()
    object NoInternet : DataError()
    object Forbidden : DataError()
    object BadRequest : DataError()
    object InvalidEmail : DataError()
    object PasswordMismatch : DataError()
    object PasswordStrength : DataError()
    class ParseFailure(var error: Exception?) : DataError()
    class DatabaseFailure(var error: Exception?) : DataError()
    class CacheFailure(var error: Exception?) : DataError()
    class NetworkFailure(var error: Exception?) : DataError()
    class UnknownReason(var error: Exception?) : DataError()
    class Other(var errors: FieldErrors) : DataError()

    override fun getLocalizedMessage(): String {

        return if (this is NetworkFailure
            && this.error != null
            && this.error is NetworkError
            && (this.error as NetworkError).fieldErrors.size > 0) {
            (this.error as NetworkError).fieldErrors.joinToString(separator = ", ")
        } else
            super.getLocalizedMessage() ?: ""
    }
}