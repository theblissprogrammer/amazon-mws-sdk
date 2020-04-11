package com.theblissprogrammer.amazon.sdk.stores.reports

import com.theblissprogrammer.amazon.sdk.common.LiveResult
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.stores.reports.models.RequestReport

/**
 * Created by ahmed.saad on 2019-09-26.
 * Copyright © 2019. All rights reserved.
 */
class ReportsRoomStore(private val reportDAO: ReportDAO?): ReportsCacheStore {

    override fun getReports(): LiveResult<List<RequestReport>> {
        val item = reportDAO?.fetchAll()

        return if (item == null) {
            LiveResult.failure(DataError.NonExistent)
        } else {
            LiveResult.success(item)
        }
    }

    override fun createOrUpdate(report: RequestReport) {
        reportDAO?.insert(report)
    }

    override fun createOrUpdate(vararg report: RequestReport) {
        reportDAO?.insert(*report)
    }

    override fun deleteById(id: String) {
        reportDAO?.delete(id)
    }
}