package org.rowanacm.android.model

import java.util.*

data class CalendarItem(
        var title: String,
        var description: String,
        var location: String,
        var repeatRule: String,
        var startTime: Calendar,
        var lengthMinutes: Int
)