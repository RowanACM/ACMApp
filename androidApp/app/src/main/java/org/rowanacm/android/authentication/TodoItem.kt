package org.rowanacm.android.authentication

import com.google.gson.annotations.SerializedName

data class TodoItem(
        var text: String = "",

        @SerializedName("completed")
        var isCompleted: Boolean = false
)