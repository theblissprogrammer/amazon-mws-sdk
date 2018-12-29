package com.theblissprogrammer.amazon.sdk.enums

/**
 * Created by ahmedsaad on 2017-11-16.
 * Copyright © 2017. All rights reserved.
 */
enum class MarketplaceType(val id: String) {
    CA("A2EUQ1WTGCTBG2"),
    US("ATVPDKIKX0DER"),
    MX("A1AM78C64UM0Y8"),

    ES("A1RKKUPIHCS9HS"),
    UK("A1F83G8C2ARO7P"),
    FR("A13V1IB3VIYZZH"),
    DE("A1PA6795UKMFR9"),
    IT("APJ6JRA9NG5V4"),

    BR("A2Q3Y263D00KWC"),
    IN("A21TJRUUN4KGV"),
    CN("AAHKV2X7AFYLW"),
    JP("A1VC38T7YXB528"),
    AU("A39IBJ37TRP1C6");

    val region: RegionType
        get() {
            return when (this) {
                CA, US, MX -> RegionType.NA
                ES, UK, FR, DE, IT -> RegionType.EU
                BR -> RegionType.BR
                IN -> RegionType.IN
                CN -> RegionType.CN
                JP -> RegionType.JP
                AU -> RegionType.AU
            }
        }

    val amazonBaseURL: String
        get() {
            return when (this) {
                CA -> "https://www.amazon.ca"
                US -> "https://www.amazon.com"
                MX -> "https://www.amazon.com.mx"
                ES -> "https://www.amazon.es"
                UK -> "https://www.amazon.co.uk"
                FR -> "https://www.amazon.fr"
                DE -> "https://www.amazon.de"
                IT -> "https://www.amazon.it"
                BR -> "https://www.amazon.com.br"
                IN -> "https://www.amazon.in"
                CN -> "https://www.amazon.cn"
                JP -> "https://www.amazon.co.jp"
                AU -> "https://www.amazon.com.au"
            }
        }

    val sellerRatingURL: String
        get() {
            return "https://www.sellerratings.com/amazon/" + when (this) {
                CA -> "canada"
                US -> "usa"
                MX -> "mexico"
                ES -> "spain"
                UK -> "uk"
                FR -> "france"
                DE -> "germany"
                IT -> "italy"
                BR -> "brazil"
                IN -> "india"
                CN -> "china"
                JP -> "japan"
                AU -> "australia"
            }
        }

    val salesChannel: String
        get() {
            return when (this) {
                CA -> "Amazon.ca"
                US -> "Amazon.com"
                MX -> "Amazon.com.mx"
                ES -> "Amazon.es"
                UK -> "Amazon.co.uk"
                FR -> "Amazon.fr"
                DE -> "Amazon.de"
                IT -> "Amazon.it"
                BR -> "Amazon.com.br"
                IN -> "Amazon.in"
                CN -> "Amazon.cn"
                JP -> "Amazon.co.jp"
                AU -> "Amazon.com.au"
            }
        }
}

fun marketplaceFromSalesChannel(salesChannel: String?): MarketplaceType? {
    return MarketplaceType.values().firstOrNull { it.salesChannel == salesChannel }
}

fun marketplaceFromId(id: String?): MarketplaceType? {
    return MarketplaceType.values().firstOrNull { it.id == id }
}
