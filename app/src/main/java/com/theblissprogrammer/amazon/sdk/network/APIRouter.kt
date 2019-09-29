package com.theblissprogrammer.amazon.sdk.network

import android.net.Uri
import com.theblissprogrammer.amazon.sdk.account.models.LoginModels
import com.theblissprogrammer.amazon.sdk.enums.MarketplaceType
import com.theblissprogrammer.amazon.sdk.enums.RegionType
import com.theblissprogrammer.amazon.sdk.stores.orders.models.OrderModels
import com.theblissprogrammer.amazon.sdk.stores.reports.models.ReportModels
import com.theblissprogrammer.amazon.sdk.enums.DefaultsKeys
import com.theblissprogrammer.amazon.sdk.enums.SecurityProperty
import com.theblissprogrammer.amazon.sdk.extensions.dateFormatter
import com.theblissprogrammer.amazon.sdk.extensions.isDateToday
import com.theblissprogrammer.amazon.sdk.preferences.ConstantsType
import com.theblissprogrammer.amazon.sdk.preferences.PreferencesWorkerType
import com.theblissprogrammer.amazon.sdk.security.SecurityWorkerType
import com.theblissprogrammer.amazon.sdk.stores.inventory.models.InventoryModels
import com.theblissprogrammer.amazon.sdk.stores.subscriptions.models.SubscriptionsModels
import java.util.*


/**
 * Created by ahmedsaad on 2017-11-03.
 * Copyright © 2017. All rights reserved.
 */

sealed class APIRouter: APIRoutable() {
    class Login(private val request: LoginModels.Request) : APIRouter() {
        override val path = "/Sellers/2011-07-01"
        override val queryParameterList = {
            val map = mutableMapOf<String, String>()
            map["SellerId"] = request.sellerID
            map["MWSAuthToken"] = request.token

            map["Action"] = "ListMarketplaceParticipations"
            map["Version"] = "2011-07-01"
            map
        }()
    }

    class ReadUser : APIRouter() {
        override val path = "/Sellers/2011-07-01"
        override val queryParameterList = {
            val map = mutableMapOf<String, String>()
            map["Action"] = "ListMarketplaceParticipations"
            map["Version"] = "2011-07-01"
            map
        }()
    }

    class ReadReport(val id: String) : APIRouter() {
        override val path = "/"
        override val queryParameterList = {
            val map = mutableMapOf<String, String>()
            map["Action"] = "GetReport"
            map["Version"] = "2009-01-01"

            map["ReportId"] = id
            map
        }()
    }

    class RequestReport(val request: ReportModels.Request) : APIRouter() {
        override val path = "/"
        override val queryParameterList = {
            val map = mutableMapOf<String, String>()
            map["Action"] = "RequestReport"
            map["Version"] = "2009-01-01"

            map["ReportType"] = request.type.id

            if (request.date != null) {
                val formatter = dateFormatter()
                map["StartDate"] = formatter.format(request.date)
            }

            request.marketplaces.forEachIndexed { index, marketplace ->
                map["MarketplaceIdList.Id.${index + 1}"] = marketplace.id
            }
            map
        }()
    }

    class ReportRequestList(val request: ReportModels.ReportRequest) : APIRouter() {
        override val path = "/"
        override val queryParameterList = {
            val map = mutableMapOf<String, String>()
            map["Action"] = "GetReportRequestList"
            map["Version"] = "2009-01-01"

            // TODO: Optimize to only get specific report
            map["MaxCount"] = "100"

            if (request.requestFrom != null) {
                val formatter = dateFormatter()
                map["RequestedFromDate"] = formatter.format(request.requestFrom)
            }

            request.ids.forEachIndexed { index, id ->
                map["ReportRequestIdList.Id.${index + 1}"] = id
            }

            request.types.forEachIndexed { index, type ->
                map["ReportTypeList.Type.${index + 1}"] = type.id
            }

            request.statuses.forEachIndexed { index, status ->
                map["ReportProcessingStatusList.Status.${index + 1}"] = status.name
            }
            map
        }()
    }

    class ReadOrders(val request: OrderModels.Request) : APIRouter() {
        override val path = "/Orders/2013-09-01"
        override val queryParameterList = {
            val map = mutableMapOf<String, String>()
            map["Action"] = "ListOrders"
            map["Version"] = "2013-09-01"

            request.marketplaces.forEachIndexed { index, marketplace ->
                map["MarketplaceId.Id.${index + 1}"] = marketplace.id
            }

            if (request.id != null) {
                map["SellerOrderId"] = request.id
            } else {
                request.orderStatuses.forEachIndexed { index, orderStatus ->
                    map["OrderStatus.Status.${index + 1}"] = orderStatus.name
                }

                val formatter = dateFormatter()

                if (request.startDate == null) {
                    map["LastUpdatedAfter"] = formatter.format(request.lastSync)
                } else
                    map["CreatedAfter"] = formatter.format(request.startDate)

                if (request.endDate.before(Date())) {
                    map["CreatedBefore"] = formatter.format(request.endDate)
                }
            }
            map
        }()
    }

    class ReadNextOrders(val nextToken: String) : APIRouter() {
        override val path = "/Orders/2013-09-01"
        override val queryParameterList = {
            val map = mutableMapOf<String, String>()
            map["Action"] = "ListOrdersByNextToken"
            map["Version"] = "2013-09-01"
            map["NextToken"] = nextToken
            map
        }()
    }

    class ReadOrderItems(val id: String) : APIRouter() {
        override val path = "/Orders/2013-09-01"
        override val queryParameterList = {
            val map = mutableMapOf<String, String>()
            map["Action"] = "ListOrderItems"
            map["Version"] = "2013-09-01"
            map["AmazonOrderId"] = id
            map
        }()
    }

    class ReadNextOrderItems(val nextToken: String) : APIRouter() {
        override val path = "/Orders/2013-09-01"
        override val queryParameterList = {
            val map = mutableMapOf<String, String>()
            map["Action"] = "ListOrderItemsByNextToken"
            map["Version"] = "2013-09-01"
            map["NextToken"] = nextToken
            map
        }()
    }

    class ReadInventory(val request: InventoryModels.Request) : APIRouter() {
        override val path = "/FulfillmentInventory/2010-10-01"
        override val queryParameterList = {
            val map = mutableMapOf<String, String>()
            map["Action"] = "ListInventorySupply"
            map["Version"] = "2010-10-01"
            map["ResponseGroup"] = "Detailed"

            // MarketplaceId is only available in NA
            request.marketplace?.let {
                if (it.region == RegionType.NA)
                    map["MarketplaceId"] = it.id
            }

            if (request.skus.isNotEmpty()) {
                request.skus.forEachIndexed { index, sku ->
                    map["SellerSkus.member.${index + 1}"] = sku
                }
            } else {
                val formatter = dateFormatter()

                map["QueryStartDateTime"] = formatter.format(request.lastSync)
            }
            map
        }()
    }

    class ReadNextInventory(val nextToken: String) : APIRouter() {
        override val path = "/FulfillmentInventory/2010-10-01"
        override val queryParameterList = {
            val map = mutableMapOf<String, String>()
            map["Action"] = "ListInventorySupplyByNextToken"
            map["Version"] = "2010-10-01"
            map["NextToken"] = nextToken
            map
        }()
    }

    class RegisterDestination(val request: SubscriptionsModels.DestinationRequest): APIRouter() {
        override val path = "/Subscriptions/2013-07-01"
        override val queryParameterList = {
            val map = mutableMapOf<String, String>()
            map["Action"] = "RegisterDestination"
            map["Version"] = "2013-07-01"
            map["MarketplaceId"] = request.queue.marketplace.id
            map["Destination.DeliveryChannel"] = "SQS"
            map["Destination.AttributeList.member.1.Key"] = "sqsQueueUrl"
            map["Destination.AttributeList.member.1.Value"] = request.queue.url ?: ""

            map
        }()
    }

    class CreateSubscription(val request: SubscriptionsModels.SubscriptionRequest): APIRouter() {
        override val path = "/Subscriptions/2013-07-01"
        override val queryParameterList = {
            val map = mutableMapOf<String, String>()
            map["Action"] = "CreateSubscription"
            map["Version"] = "2013-07-01"
            map["MarketplaceId"] = request.queue.marketplace.id
            map["Subscription.NotificationType"] = request.notificationType.name
            map["Subscription.Destination.DeliveryChannel"] = "SQS"
            map["Subscription.Destination.AttributeList.member.1.Key"] = "sqsQueueUrl"
            map["Subscription.Destination.AttributeList.member.1.Value"] = request.queue.url ?: ""
            map["Subscription.IsEnabled"] = request.isEnabled.toString()

            map
        }()
    }

    override fun getURL(constants: ConstantsType,
                        preferencesWorker: PreferencesWorkerType,
                        securityWorker: SecurityWorkerType,
                        signedHelper: SignedHelperType): String {

        val region = MarketplaceType.valueOf(preferencesWorker.get(DefaultsKeys.marketplace) ?: "US").region

        val uri = Uri.parse(region.baseURL)
                .buildUpon()

        queryParameterList["AWSAccessKeyId"] = when (region) {
            RegionType.EU -> constants.euAwsAccessKeyID
            else -> constants.awsAccessKeyID
        }

        if (!queryParameterList.containsKey("SellerId"))
            queryParameterList["SellerId"] = preferencesWorker.get(DefaultsKeys.sellerID) ?: ""

        if (!queryParameterList.containsKey("MWSAuthToken"))
            queryParameterList["MWSAuthToken"] = securityWorker.get(SecurityProperty.TOKEN(region.name)) ?: ""

        queryParameterList["SignatureMethod"] = SignedRequestsHelper.HMAC_SHA256_ALGORITHM
        queryParameterList["SignatureVersion"] = "2"
        queryParameterList["Timestamp"] = timestamp

        val canonicalizedParams = SignedRequestsHelper.canonicalize(queryParameterList)

        // create the string upon which the signature is calculated
        val toSign = method.name + "\n" + region.endpoint + "\n" + path + "\n" + canonicalizedParams

        val queryParams = "?$canonicalizedParams&Signature=${signedHelper.hmac(toSign)}"

        return uri.build().toString() + path + queryParams
    }
}