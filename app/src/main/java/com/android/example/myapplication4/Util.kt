/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.myapplication4

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.text.Html
import android.text.Spanned
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import com.android.example.myapplication4.database.Transaction
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * These functions create a formatted string that can be set in a TextView.
 */

/**
 * Returns a string representing the numeric quality rating.
 */
/*fun convertNumericQualityToString(quality: Int, resources: Resources): String {
    var qualityString = resources.getString(R.string.three_ok)
    when (quality) {
        -1 -> qualityString = "--"
        0 -> qualityString = resources.getString(R.string.zero_very_bad)
        1 -> qualityString = resources.getString(R.string.one_poor)
        2 -> qualityString = resources.getString(R.string.two_soso)
        4 -> qualityString = resources.getString(R.string.four_pretty_good)
        5 -> qualityString = resources.getString(R.string.five_excellent)
    }
    return qualityString
}*/


/**
 * Take the Long milliseconds returned by the system and stored in Room,
 * and convert it to a nicely formatted string for display.
 *
 * EEEE - Display the long letter version of the weekday
 * MMM - Display the letter abbreviation of the nmotny
 * dd-yyyy - day in month and full year numerically
 * HH:mm - Hours and minutes in 24hr format
 */
@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("EEEE MMM-dd-yyyy' Time: 'HH:mm")
            .format(systemTime).toString()
}
fun giveDate(): String? {
    val cal: Calendar = Calendar.getInstance()
    val sdf = SimpleDateFormat("EEE, MMM d, yyyy")
    return sdf.format(cal.getTime())
}
/**
 * Takes a list of SleepNights and converts and formats it into one string for display.
 *
 * For display in a TextView, we have to supply one string, and styles are per TextView, not
 * applicable per word. So, we build a formatted string using HTML. This is handy, but we will
 * learn a better way of displaying this data in a future lesson.
 *
 * @param   nights - List of all SleepNights in the database.
 * @param   resources - Resources object for all the resources defined for our app.
 *
 * @return  Spanned - An interface for text that has formatting attached to it.
 *           See: https://developer.android.com/reference/android/text/Spanned
 */
fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}
fun formatNights(transactions: List<Transaction>, resources: Resources): Spanned {
    val sb = StringBuilder()
    sb.apply {
        //append(resources.getString(R.string.title))
        transactions.forEach {
            append("<br>")
           // append(resources.getString(R.string.start_time))
           // append("\t${convertLongToDateString(it.startTimeMilli)}<br>")
            if (it.income && it.amount!=0) {
                append("<b> Date: </b> ${it.dateTime} <br>")
                append(resources.getString(R.string.income_text))
                append("\t${it.amount}")
                append("<br><b>Remarks: </b>${it.commentString} ")

                append("<br>")
            }
            else if(it.expense && it.amount!=0){
                append("<b> Date: </b> ${it.dateTime} <br>")
                append(resources.getString(R.string.expense_text))
                append("\t${it.amount}")
                append("<br><b>Remarks: </b>${it.commentString} ")

                append("<br>")
            }
        }
    }
    if (VERSION.SDK_INT >= VERSION_CODES.N) {
        return Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        return HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}
