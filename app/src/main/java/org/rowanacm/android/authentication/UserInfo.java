package org.rowanacm.android.authentication;


import java.util.List;

public class UserInfo {

    private String phone_number;
    private String uid;
    private String profile_picture;
    private boolean is_admin;
    private String github_username;
    private String name;
    private String slack_username;
    private boolean on_slack;
    private boolean is_eboard;
    private boolean on_github;
    private String rowan_email;
    private int meeting_count;
    private String committee_string;
    private List<TodoItem> todo_list;

    public UserInfo() {
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phone_number = phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfilePicture() {
        return profile_picture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profile_picture = profilePicture;
    }

    public boolean getIsAdmin() {
        return is_admin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.is_admin = isAdmin;
    }

    public String getGithubUsername() {
        return github_username;
    }

    public void setGithubUsername(String githubUsername) {
        this.github_username = githubUsername;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlackUsername() {
        return slack_username;
    }

    public void setSlackUsername(String slack_username) {
        this.slack_username = slack_username;
    }

    public boolean getOnSlack() {
        return on_slack;
    }

    public void setOnSlack(boolean onSlack) {
        this.on_slack = onSlack;
    }

    public boolean getIsEboard() {
        return is_eboard;
    }

    public void setIsEboard(boolean isEboard) {
        this.is_eboard = isEboard;
    }

    public boolean getOnGithub() {
        return on_github;
    }

    public void setOnGithub(boolean on_github) {
        this.on_github = on_github;
    }

    public String getRowanEmail() {
        return rowan_email;
    }

    public void setRowanEmail(String rowanEmail) {
        this.rowan_email = rowanEmail;
    }

    public int getMeetingCount() {
        return meeting_count;
    }

    public void setMeetingCount(int meeting_count) {
        this.meeting_count = meeting_count;
    }

    public String getCommitteeText() {
        return committee_string;
    }

    public void setCommitteeText(String committee_text) {
        this.committee_string = committee_text;
    }

    public List<TodoItem> getTodoList() {
        return todo_list;
    }

    public void setTodoList(List<TodoItem> todo_list) {
        this.todo_list = todo_list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInfo userInfo = (UserInfo) o;

        if (is_admin != userInfo.is_admin) return false;
        if (on_slack != userInfo.on_slack) return false;
        if (is_eboard != userInfo.is_eboard) return false;
        if (on_github != userInfo.on_github) return false;
        if (meeting_count != userInfo.meeting_count) return false;
        if (phone_number != null ? !phone_number.equals(userInfo.phone_number) : userInfo.phone_number != null)
            return false;
        if (uid != null ? !uid.equals(userInfo.uid) : userInfo.uid != null) return false;
        if (profile_picture != null ? !profile_picture.equals(userInfo.profile_picture) : userInfo.profile_picture != null)
            return false;
        if (github_username != null ? !github_username.equals(userInfo.github_username) : userInfo.github_username != null)
            return false;
        if (name != null ? !name.equals(userInfo.name) : userInfo.name != null) return false;
        if (slack_username != null ? !slack_username.equals(userInfo.slack_username) : userInfo.slack_username != null)
            return false;
        if (rowan_email != null ? !rowan_email.equals(userInfo.rowan_email) : userInfo.rowan_email != null)
            return false;
        if (committee_string != null ? !committee_string.equals(userInfo.committee_string) : userInfo.committee_string != null)
            return false;
        return todo_list != null ? todo_list.equals(userInfo.todo_list) : userInfo.todo_list == null;

    }

    @Override
    public int hashCode() {
        int result = phone_number != null ? phone_number.hashCode() : 0;
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (profile_picture != null ? profile_picture.hashCode() : 0);
        result = 31 * result + (is_admin ? 1 : 0);
        result = 31 * result + (github_username != null ? github_username.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (slack_username != null ? slack_username.hashCode() : 0);
        result = 31 * result + (on_slack ? 1 : 0);
        result = 31 * result + (is_eboard ? 1 : 0);
        result = 31 * result + (on_github ? 1 : 0);
        result = 31 * result + (rowan_email != null ? rowan_email.hashCode() : 0);
        result = 31 * result + meeting_count;
        result = 31 * result + (committee_string != null ? committee_string.hashCode() : 0);
        result = 31 * result + (todo_list != null ? todo_list.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "phone_number='" + phone_number + '\'' +
                ", uid='" + uid + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                ", is_admin=" + is_admin +
                ", github_username='" + github_username + '\'' +
                ", name='" + name + '\'' +
                ", slack_username='" + slack_username + '\'' +
                ", on_slack=" + on_slack +
                ", is_eboard=" + is_eboard +
                ", on_github=" + on_github +
                ", rowan_email='" + rowan_email + '\'' +
                ", meeting_count=" + meeting_count +
                ", committee_string='" + committee_string + '\'' +
                ", todo_list=" + todo_list +
                '}';
    }
}