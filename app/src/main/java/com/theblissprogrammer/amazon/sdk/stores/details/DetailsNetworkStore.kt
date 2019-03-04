package com.theblissprogrammer.amazon.sdk.stores.details

import com.theblissprogrammer.amazon.sdk.common.DeferredResult
import com.theblissprogrammer.amazon.sdk.common.Result
import com.theblissprogrammer.amazon.sdk.errors.DataError
import com.theblissprogrammer.amazon.sdk.extensions.coroutineNetwork
import com.theblissprogrammer.amazon.sdk.logging.LogHelper
import com.theblissprogrammer.amazon.sdk.network.HTTPServiceType
import com.theblissprogrammer.amazon.sdk.stores.details.models.Detail
import org.jsoup.Jsoup

/**
 * Created by ahmed.saad on 2019-03-04.
 * Copyright © 2019. All rights reserved.
 */
class DetailsNetworkStore(val httpService: HTTPServiceType): DetailsStore {

    private val userAgent = "Mozilla/5.0 (BlackBerry; U; BlackBerry 9900; en-US) AppleWebKit/534.11+ (KHTML, like Gecko)" +
            " Version/7.0.0.187 Mobile Safari/534.11+"

    override fun fetchAsync(asin: String): DeferredResult<Detail> {
        return coroutineNetwork <Detail> {
            val detail = fetchDetails(asin) ?: return@coroutineNetwork Result.failure(DataError.UnknownReason(null))

            detail.description = fetchDescription(asin)
            detail.features = fetchFeatures(asin)
            detail.images = fetchImages(asin)

            Result.success(detail)
        }
    }

    private fun fetchDescription(asin: String): String? {

        val response = httpService.get(
            url = "https://www.amazon.com/gp/aw/d/$asin",
            parameters = mapOf(Pair("d", "d"), Pair("pd", "1")),
            headers = mapOf(Pair("User-Agent", userAgent))
        )

        val value = response.value
        if (value == null || !response.isSuccess) {
            return null
        }

        return try {
            // Parse response data
            val jsoup = Jsoup.parse(value.data)

            jsoup.getElementsByClass("dpDynamicSectionBody").firstOrNull()?.html()
        } catch(e: Exception) {
            LogHelper.e(messages = *arrayOf("An error occurred while parsing item details for $asin: " +
                    "${e.localizedMessage ?: ""}."))
            null
        }
    }

    private fun fetchFeatures(asin: String): String? {

        val response = httpService.get(
            url = "https://www.amazon.com/gp/aw/d/$asin",
            parameters = mapOf(Pair("d", "f"), Pair("pd", "1")),
            headers = mapOf(Pair("User-Agent", userAgent))
        )

        val value = response.value
        if (value == null || !response.isSuccess) {
            return null
        }

        return try {
            // Parse response data
            val jsoup = Jsoup.parse(value.data)

            jsoup.getElementsByClass("dpDynamicSectionBody").firstOrNull()?.html()
        } catch(e: Exception) {
            LogHelper.e(messages = *arrayOf("An error occurred while parsing item details for $asin: " +
                    "${e.localizedMessage ?: ""}."))
            null
        }
    }

    private fun fetchDetails(asin: String): Detail? {
        val response = httpService.get(
            url = "https://www.amazon.com/gp/aw/d/$asin",
            parameters = mapOf(Pair("pd", "1")),
            headers = mapOf(Pair("User-Agent", userAgent))
        )

        val value = response.value
        if (value == null || !response.isSuccess) {
            return null
        }

        return try {
            // Parse response data
            val jsoup = Jsoup.parse(value.data)

            val title = jsoup.title().split(":Amazon:")

            if (title[0].contains("Amazon.com: Online Shopping")) {
                return null
            }

            // If Robot Check.. pause and try again
            if (title[0].contains("Robot Check")) {
                Thread.sleep(2000)
                return fetchDetails(asin)
            }

            val values = jsoup.getElementsByClass("dpSectionBodyText").lastOrNull()?.html()
                ?.replace("&nbsp;", "")?.split("<br>")

            Detail(
                asin = asin,
                title = if (title.isNotEmpty()) title.first() else "",
                manufacturer = values?.firstOrNull { it.contains("Manufacturer:") }
                    ?.replace("Manufacturer:", "")?.trim(),
                manufacturerReference = values?.firstOrNull { it.contains("Manufacturer reference:") }
                    ?.replace("Manufacturer reference:", "")?.trim(),
                weight = values?.firstOrNull { it.contains("Weight:") }
                    ?.replace(Regex(".*?Weight:"), "")?.trim(),
                bsr = values?.firstOrNull { it.contains("Sales Rank:") }
                    ?.replace(Regex(".*?Sales Rank:"), "")?.trim()?.toIntOrNull(),
                category = if (title.size > 1) title.lastOrNull() else null
            )
        } catch(e: Exception) {
            LogHelper.e(messages = *arrayOf("An error occurred while parsing item details for $asin: " +
                    "${e.localizedMessage ?: ""}."))
            null
        }
    }


    private fun fetchImages(asin: String): List<String> {

        val images = arrayListOf<String?>()

        var index = 1
        var max = 0

        do {
            val response = httpService.get(
                url = "https://www.amazon.com/gp/aw/d/$asin",
                parameters = mapOf(Pair("in", "$index")),
                headers = mapOf(Pair("User-Agent", userAgent))
            )

            val value = response.value
            if (value == null || !response.isSuccess) {
                break
            }

            try {
                // Parse response data
                val jsoup = Jsoup.parse(value.data)



                val image = jsoup.getElementById("dpImage").getElementsByTag("img").firstOrNull()?.attr("src")
                    ?.replace(Regex("\\._.*?_\\.jpg"), ".jpg")

                images.add(image)

                if (max == 0) {
                    val imageCounter = jsoup.getElementById("imageCounter").text().split("of")
                    max = imageCounter.lastOrNull()?.trim()?.toIntOrNull() ?: 0
                }
            } catch (e: Exception) {
                continue
            } finally {
                index ++
            }
        } while(index < max + 1)

        return images.filterNotNull()
    }
}