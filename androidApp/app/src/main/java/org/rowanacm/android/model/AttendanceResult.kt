package org.rowanacm.android.model

import com.google.gson.annotations.SerializedName

data class AttendanceResult(
        var message: String,
        var status: String,

        @SerializedName("response_code")
        var responseCode: Int
)