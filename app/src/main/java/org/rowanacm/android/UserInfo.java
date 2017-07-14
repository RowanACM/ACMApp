package org.rowanacm.android;


public class UserInfo {

    private String phone_number;
    private String uid;
    private String profile_picture;
    private Boolean is_admin;
    private String github_username;
    private String name;
    private String slack_username;
    private Boolean on_slack;
    private Boolean is_eboard;
    private Boolean on_github;
    private String rowan_email;

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

    public Boolean getIsAdmin() {
        return is_admin;
    }

    public void setIsAdmin(Boolean isAdmin) {
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

    public String getSlack_username() {
        return slack_username;
    }

    public void setSlack_username(String slack_username) {
        this.slack_username = slack_username;
    }

    public Boolean getOnSlack() {
        return on_slack;
    }

    public void setOnSlack(Boolean onSlack) {
        this.on_slack = onSlack;
    }

    public Boolean getIsEboard() {
        return is_eboard;
    }

    public void setIsEboard(Boolean isEboard) {
        this.is_eboard = isEboard;
    }

    public Boolean getOn_github() {
        return on_github;
    }

    public void setOn_github(Boolean on_github) {
        this.on_github = on_github;
    }

    public String getRowanEmail() {
        return rowan_email;
    }

    public void setRowanEmail(String rowanEmail) {
        this.rowan_email = rowanEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInfo userInfo = (UserInfo) o;

        if (phone_number != null ? !phone_number.equals(userInfo.phone_number) : userInfo.phone_number != null)
            return false;
        if (uid != null ? !uid.equals(userInfo.uid) : userInfo.uid != null) return false;
        if (profile_picture != null ? !profile_picture.equals(userInfo.profile_picture) : userInfo.profile_picture != null)
            return false;
        if (is_admin != null ? !is_admin.equals(userInfo.is_admin) : userInfo.is_admin != null)
            return false;
        if (github_username != null ? !github_username.equals(userInfo.github_username) : userInfo.github_username != null)
            return false;
        if (name != null ? !name.equals(userInfo.name) : userInfo.name != null) return false;
        if (slack_username != null ? !slack_username.equals(userInfo.slack_username) : userInfo.slack_username != null)
            return false;
        if (on_slack != null ? !on_slack.equals(userInfo.on_slack) : userInfo.on_slack != null)
            return false;
        if (is_eboard != null ? !is_eboard.equals(userInfo.is_eboard) : userInfo.is_eboard != null)
            return false;
        if (on_github != null ? !on_github.equals(userInfo.on_github) : userInfo.on_github != null)
            return false;
        if (rowan_email != null ? !rowan_email.equals(userInfo.rowan_email) : userInfo.rowan_email != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = phone_number != null ? phone_number.hashCode() : 0;
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (profile_picture != null ? profile_picture.hashCode() : 0);
        result = 31 * result + (is_admin != null ? is_admin.hashCode() : 0);
        result = 31 * result + (github_username != null ? github_username.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (slack_username != null ? slack_username.hashCode() : 0);
        result = 31 * result + (on_slack != null ? on_slack.hashCode() : 0);
        result = 31 * result + (is_eboard != null ? is_eboard.hashCode() : 0);
        result = 31 * result + (on_github != null ? on_github.hashCode() : 0);
        result = 31 * result + (rowan_email != null ? rowan_email.hashCode() : 0);
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
                '}';
    }
}