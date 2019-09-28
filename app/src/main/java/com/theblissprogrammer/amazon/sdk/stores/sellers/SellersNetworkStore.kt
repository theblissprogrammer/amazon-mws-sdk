package com.theblissprogrammer.amazon.sdk.stores.sellers

import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetworkAsync
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.Seller
import com.theblissprogrammer.amazon.sdk.stores.sellers.models.SellerModels
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.failure
import com.theblissprogrammer.amazon.sdk.common.Result.Companion.success
import com.theblissprogrammer.amazon.sdk.common.initDataError
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.network.HTTPServiceType
import org.jsoup.Jsoup

/**
 * Created by ahmedsaad on 2018-08-03.
 * Copyright (c) 2018. All rights reserved.
 **/
class SellersNetworkStore(val httpService: HTTPServiceType): SellersStore {

    override fun fetch(request: SellerModels.Request) : DeferredResult<Seller> {

        return coroutineNetworkAsync <Seller> {
            val response = httpService.get(request.marketplace.amazonBaseURL + "/sp",
                    parameters = mapOf(Pair("seller", request.id)))

            if (response.value == null || !response.isSuccess) {
                val exception = initDataError(response.error)
                LogHelper.e(messages = *arrayOf("An error occurred while fetching seller profile: " +
                        exception.localizedMessage))

                failure(exception)
            } else {
                try {
                    // Parse response data
                    val doc = Jsoup.parse(response.value?.data)
                    val name = doc.getElementById("sellerName")?.text()
                    val logo = doc.getElementById("sellerLogo")?.attr("src")
                    val storefront = doc.getElementById("storefront-link")
                            ?.getElementsByTag("a")?.firstOrNull()?.attr("href")

                    val rating = try {
                        doc.getElementById("seller-feedback-summary")
                                ?.getElementsByClass("a-icon-alt")?.firstOrNull()?.text()
                                ?.replace(regex = Regex("\\s*out.*"), replacement = "")?.toDouble()
                    } catch (e: NumberFormatException) {
                        null // Default
                    }

                    val feedBackString = doc.getElementById("seller-feedback-summary")
                            ?.getElementsByClass("feedback-detail-description")
                            ?.firstOrNull()?.text()

                    val feedbackPercent = try {
                        feedBackString
                                ?.replace(regex = Regex("\\s*%\\s*positive.*"), replacement = "")?.toInt()
                    } catch (e: NumberFormatException) {
                        null // Default
                    }

                    val feedbackRating = try {
                        feedBackString
                                ?.replace(regex = Regex(".*\\("), replacement = "")
                                ?.replace(regex = Regex("\\s*ratings.*"), replacement = "")?.toInt()
                    } catch (e: NumberFormatException) {
                        null // Default
                    }

                    val rank = fetchSellerRank(request, sellerName = name)
                    val payload = Seller(
                            id = request.id,
                            marketplace = request.marketplace,
                            name = name,
                            logo = logo,
                            storefrontUrl = request.marketplace.amazonBaseURL + storefront,
                            rating = rating,
                            feedbackPercent = feedbackPercent,
                            numberOfRatings = feedbackRating,
                            rank = rank
                    )

                    success<Seller>(payload)
                } catch (e: Exception) {
                    LogHelper.e(messages = *arrayOf("An error occurred while parsing Amazon seller profile: " +
                            e.localizedMessage))
                    failure<Seller>(DataError.ParseFailure(e))
                }
            }
        }
    }

    private fun fetchSellerRank(request: SellerModels.Request, sellerName: String?): String? {
        if (sellerName.isNullOrEmpty()) return null

        val response = httpService.get(request.marketplace.sellerRatingURL,
                parameters = mapOf(Pair("name", sellerName)))

        if (response.value == null || !response.isSuccess) {
            val exception = initDataError(response.error)
            LogHelper.e(messages = *arrayOf("An error occurred while fetching seller rank: " +
                    exception.localizedMessage))

            return null
        }

        val doc = Jsoup.parse(response.value?.data)
        val rank = doc.getElementById("report")
                ?.getElementsByTag("tr")?.firstOrNull {
                    it.getElementsByTag("td")?.getOrNull(1)?.text() == sellerName
                }?.getElementsByClass("rank")?.text()

        return rank
    }
}