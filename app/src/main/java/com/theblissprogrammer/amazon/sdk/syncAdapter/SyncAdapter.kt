package com.theblissprogrammer.amazon.sdk.syncAdapter

import android.accounts.Account
import android.content.*
import android.os.Bundle

/**
 * Created by ahmed.saad on 2019-09-30.
 * Copyright © 2019. All rights reserved.
 */
/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
class SyncAdapter @JvmOverloads constructor(
        context: Context,
        autoInitialize: Boolean,
        /**
         * Using a default argument along with @JvmOverloads
         * generates constructor for both method signatures to maintain compatibility
         * with Android 3.0 and later platform versions
         */
        allowParallelSyncs: Boolean = false
) : AbstractThreadedSyncAdapter(context, autoInitialize, allowParallelSyncs) {

    /**
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
    override fun onPerformSync(
            account: Account,
            extras: Bundle,
            authority: String,
            provider: ContentProviderClient,
            syncResult: SyncResult
    ) {

        Thread.sleep(1000*60)
        val name = account.name

    }

}