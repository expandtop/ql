package com.proxy.base.util

import android.content.Context
import com.proxy.base.R
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

object TimeFormatter {

    fun formatHomeTime(context: Context, second: Long): String {
        return with(StringBuilder()) {
            if (second > 0) {
                when {
                    second > TimeUnit.DAYS.toSeconds(3) -> {
                        val day = ceil(second / TimeUnit.DAYS.toSeconds(1).toDouble()).toInt()
                        append("$day").append(context.getString(R.string.unit_day))
                    }

                    else -> {
                        val hoursItem = formatItem(second, TimeUnit.HOURS.toSeconds(1))
                        if (hoursItem.first >= 0) {
                            append(fullItem("${hoursItem.first}")).append(":")
                        }
                        val minutesItem =
                            formatItem(hoursItem.second, TimeUnit.MINUTES.toSeconds(1))
                        if (minutesItem.first >= 0) {
                            append(fullItem("${minutesItem.first}")).append(":")
                        }
                        append(fullItem("${minutesItem.second}"))
                    }
                }
            }
            toString()
        }
    }

    private fun formatItem(time: Long, itemSize: Long): Pair<Long, Long> {
        if (time > itemSize) {
            return (time / itemSize) to (time % itemSize)
        }
        return 0L to time
    }

    private fun fullItem(str: String): String {
        if (str.length >= 2) return str
        return "0$str"
    }
}