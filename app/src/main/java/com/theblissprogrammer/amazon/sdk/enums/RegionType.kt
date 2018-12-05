package com.theblissprogrammer.amazon.sdk.enums


/**
 * Created by ahmedsaad on 2017-11-16.
 * Copyright © 2017. All rights reserved.
 */
enum class RegionType(val endpoint: String) {
    NA("mws.amazonservices.com"),
    BR("mws.amazonservices.com"),
    EU("mws-eu.amazonservices.com"),
    IN("mws.amazonservices.in"),
    CN("mws.amazonservices.com.cn"),
    JP("mws.amazonservices.jp"),
    AU("mws.amazonservices.com.au");

    val baseURL: String
        get() = "https://${this.endpoint}"

    val registerURL: String
        get() {
            return when (this) {
                NA -> "https://sellercentral.amazon.com/ap/signin?openid.assoc_handle=amzn_sc_android_mobile_us&openid.mode=checkid_setup&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&pageId=sc_mobileapp&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&language=en_US&openid.return_to=https%3A%2F%2Fsellercentral.amazon.com%2Fgp%2Fmws%2Fregistration%2Fregister.html%3F*Version*%3D1%26*entries*%3D0%26ie%3DUTF8%26signInPageDisplayed%3D1%26devAuth%3D1&openid.pape.max_auth_age=0"
                EU -> "https://sellercentral.amazon.co.uk/ap/signin?openid.assoc_handle=amzn_sc_android_mobile_uk&openid.mode=checkid_setup&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&pageId=sc_mobileapp&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&language=en_US&openid.return_to=https%3A%2F%2Fsellercentral.amazon.co.uk%2Fgp%2Fmws%2Fregistration%2Fregister.html%3F*Version*%3D1%26*entries*%3D0%26ie%3DUTF8%26signInPageDisplayed%3D1%26devAuth%3D1&openid.pape.max_auth_age=0"
                IN -> "https://sellercentral.amazon.in/gp/mws/registration/register.html"
                JP -> "https://sellercentral-japan.amazon.com/gp/mws/registration/register.html"
                CN -> "https://mai.amazon.cn/ap/signin?openid.assoc_handle=amzn_sc_android_mobile_cn&openid.mode=checkid_setup&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&pageId=sc_mobileapp&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&language=en_US&openid.return_to=https%3A%2F%2Fmai.amazon.cn%2Fgp%2Fmws%2Fregistration%2Fregister.html%3F*Version*%3D1%26*entries*%3D0%26ie%3DUTF8%26signInPageDisplayed%3D1%26devAuth%3D1&openid.pape.max_auth_age=0"
                else -> ""
            }
        }
}

