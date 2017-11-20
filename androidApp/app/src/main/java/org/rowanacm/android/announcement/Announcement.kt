package org.rowanacm.android.announcement

import android.text.format.DateUtils
import org.rowanacm.android.Searchable
import java.io.Serializable
import java.util.*

/**
 * An message by Rowan ACM.
 * Contains a message and committee
 */

data class Announcement(
        var title: String,
        var text: String,
        var snippet: String,
        var committee: String,
        var icon: String? = null,
        var url: String? = null,
        var timestamp: Long
) : Serializable, Comparable<Announcement>, Searchable {

    /**
     * The newest announcements comes first in a sorted list
     * @return -1 if this announcement was created after the parameter
     */
    override fun compareTo(other: Announcement): Int {
        return other.timestamp.compareTo(timestamp)
    }

    override fun search(search: String?): Boolean {
        if (search == null || search.isEmpty()) {
            return true
        }

        val formattedSearch = search.toLowerCase()
        return snippet.toLowerCase().contains(formattedSearch) ||
                committee.toLowerCase().contains(formattedSearch) ||
                url != null && url!!.toLowerCase().contains(formattedSearch) ||
                text.toLowerCase().contains(formattedSearch) ||
                title.toLowerCase().contains(formattedSearch)
    }

    fun getRelativeDate(): String {
        val timestamp = timestamp * 1000
        val now = Date().time
        return DateUtils.getRelativeTimeSpanString(timestamp, now, DateUtils.SECOND_IN_MILLIS).toString()
    }

}
