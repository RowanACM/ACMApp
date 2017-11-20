package org.rowanacm.android.authentication

import com.google.gson.annotations.SerializedName

data class UserInfo(
        val uid: String,
        var name: String,

        @SerializedName("rowan_email")
        var rowanEmail: String,

        @SerializedName("meeting_count")
        var meetingCount: Int,

        @SerializedName("is_admin")
        var isAdmin: Boolean,

        @SerializedName("is_eboard")
        var isEboard: Boolean,

        @SerializedName("on_slack")
        var onSlack: Boolean,

        @SerializedName("on_github")
        var onGithub: Boolean,

        @SerializedName("github_username")
        var githubUsername: String? = null,

        @SerializedName("slack_username")
        var slackUsername: String? = null,

        @SerializedName("phone_number")
        var phoneNumber: String? = null,

        @SerializedName("profile_picture")
        var profilePicture: String? = null,

        @SerializedName("committee_text")
        var committeeText: String,

        @SerializedName("todo_list")
        var todoList: List<TodoItem>
)