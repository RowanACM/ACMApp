package org.rowanacm.android.announcement;

import android.support.annotation.NonNull;

import org.rowanacm.android.Searchable;

import java.io.Serializable;

/**
 * An message by Rowan ACM.
 * Contains a message and committee
 */

public class Announcement implements Serializable, Comparable<Announcement>, Searchable {
    private String author;
    private String committee;
    private String date;
    private String subj;
    private String text;
    private String title;
    private long timestamp;
    private String imageUrl;
    private String url;

    public Announcement() {
    }

    public Announcement(String author, String committee, String date, String subj, String text, String title, long timestamp, String imageUrl, String url) {
        this.author = author;
        this.committee = committee;
        this.date = date;
        this.subj = subj;
        this.text = text;
        this.title = title;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
        this.url = url;
    }

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
        return  (author != null && author.toLowerCase().contains(search))       ||
                (committee != null && committee.toLowerCase().contains(search)) ||
                (subj != null && subj.toLowerCase().contains(search))           ||
                (text != null && text.toLowerCase().contains(search))           ||
                (title != null && title.toLowerCase().contains(search));
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCommittee() {
        return committee;
    }

    public void setCommittee(String committee) {
        this.committee = committee;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubj() {
        return subj;
    }

    public void setSubj(String subj) {
        this.subj = subj;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Announcement that = (Announcement) o;

        if (timestamp != that.timestamp) return false;
        if (author != null ? !author.equals(that.author) : that.author != null) return false;
        if (committee != null ? !committee.equals(that.committee) : that.committee != null)
            return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (subj != null ? !subj.equals(that.subj) : that.subj != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (imageUrl != null ? !imageUrl.equals(that.imageUrl) : that.imageUrl != null)
            return false;
        return url != null ? url.equals(that.url) : that.url == null;

    }

    @Override
    public int hashCode() {
        int result = author != null ? author.hashCode() : 0;
        result = 31 * result + (committee != null ? committee.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (subj != null ? subj.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}
