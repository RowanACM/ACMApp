package org.rowanacm.android.announcement;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import org.rowanacm.android.Searchable;

import java.io.Serializable;
import java.util.Date;

/**
 * An message by Rowan ACM.
 * Contains a message and committee
 */

public class Announcement implements Serializable, Comparable<Announcement>, Searchable {
    private String committee, icon, title, text, snippet, url;
    private long timestamp;

    public Announcement() {
    }

    /**
     * The newest announcements comes first in a sorted list
     * @return -1 if this announcement was created after the parameter
     */
    @Override
    public int compareTo(@NonNull Announcement announcement) {
        return ((Long)announcement.timestamp).compareTo(timestamp);
    }

    @Override
    public boolean search(String search) {
        if (search == null || search.isEmpty()) {
            return true;
        }

        search = search.toLowerCase();
        return  (snippet != null && snippet.toLowerCase().contains(search))       ||
                (committee != null && committee.toLowerCase().contains(search)) ||
                (url != null && url.toLowerCase().contains(search))           ||
                (text != null && text.toLowerCase().contains(search))           ||
                (title != null && title.toLowerCase().contains(search));
    }

    public String getRelativeDate() {
        long timestamp = getTimestamp() * 1000;
        long now = new Date().getTime();
        return DateUtils.getRelativeTimeSpanString(timestamp, now, DateUtils.SECOND_IN_MILLIS).toString();
    }

    public String getCommittee() {
        return committee;
    }

    public void setCommittee(String committee) {
        this.committee = committee;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Announcement that = (Announcement) o;

        if (timestamp != that.timestamp) return false;
        if (committee != null ? !committee.equals(that.committee) : that.committee != null)
            return false;
        if (icon != null ? !icon.equals(that.icon) : that.icon != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (snippet != null ? !snippet.equals(that.snippet) : that.snippet != null) return false;
        return url != null ? url.equals(that.url) : that.url == null;

    }

    @Override
    public int hashCode() {
        int result = committee != null ? committee.hashCode() : 0;
        result = 31 * result + (icon != null ? icon.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (snippet != null ? snippet.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "committee='" + committee + '\'' +
                ", icon='" + icon + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", snippet='" + snippet + '\'' +
                ", url='" + url + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
