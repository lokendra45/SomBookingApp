package com.fourteen.sombookingapp.utils

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/**
 * Utility functions for date and time calculations using kotlinx-datetime.
 */
object DateTimeUtils {

    data class DateOption(
        val isoDate: String,      // e.g. "2026-10-25"
        val displayDate: String   // e.g. "Mon, 25 Oct"
    )

    fun getUpcomingDateOptions(count: Int = 7): ImmutableList<DateOption> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return (0 until count).map { i ->
            val date = today.plus(i, DateTimeUnit.DAY)
            val dayOfWeek = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
            val month = date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
            DateOption(
                isoDate = date.toString(),
                displayDate = "$dayOfWeek, ${date.day} $month"
            )
        }.toImmutableList()
    }
}
