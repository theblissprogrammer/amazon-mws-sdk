package com.theblissprogrammer.amazon.sdk.extensions

/**
 * Created by ahmed.saad on 2019-03-05.
 * Copyright © 2019. All rights reserved.
 */

fun String.makeCSVCompatible(): String {
    return "\"${this.replace(Regex("[\"]"), "")}\""
}