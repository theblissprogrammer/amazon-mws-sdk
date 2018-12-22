package com.theblissprogrammer.amazon.sdk.extensions

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ahmedsaad on 2018-01-05.
 * Copyright © 2017. All rights reserved.
 */

fun Date.shortString(): String {
    val outputFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    outputFmt.timeZone = TimeZone.getTimeZone("GMT")

    return outputFmt.format(this)
}

fun Date.realmString(): String {
    val outputFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)
    outputFmt.timeZone = TimeZone.getTimeZone("GMT")

    return outputFmt.format(this)
}

fun Date.add(field: Int = Calendar.DATE, value: Int = 0): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(field, value)
    return cal.time
}

val Date.isDateInWeekend: Boolean
    get() {
        val cal = Calendar.getInstance()
        cal.time = this

        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
    }

val Date.isDateInToday: Boolean
    get() {
        return this.startOfDay() == Date().startOfDay()
    }

fun Date.startOfDay(): Date {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"))
    cal.time = this
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    return cal.time
}

fun Date.endOfDay(): Date {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"))

    cal.time = this
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MILLISECOND, 59)

    return cal.time
}