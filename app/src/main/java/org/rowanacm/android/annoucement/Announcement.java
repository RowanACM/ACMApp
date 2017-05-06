package org.rowanacm.android.annoucement;

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

    public Announcement() {
    }

    public Announcement(String author, String committee, String date, String subj, String text, String title, long timestamp) {
        this.author = author;
        this.committee = committee;
        this.date = date;
        this.subj = subj;
        this.text = text;
        this.title = title;
        this.timestamp = timestamp;
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

    @Override
    public int compareTo(@NonNull Announcement announcement) {
        return ((Long)announcement.timestamp).compareTo(timestamp);
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "author='" + author + '\'' +
                ", committee='" + committee + '\'' +
                ", date='" + date + '\'' +
                ", subj='" + subj + '\'' +
                ", text='" + text + '\'' +
                ", title='" + title + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean search(String search) {
        if (search == null || search.isEmpty()) {
            return true;
        }

        search = search.toLowerCase();
        return author.toLowerCase().contains(search) ||
                committee.toLowerCase().contains(search) ||
                date.toLowerCase().contains(search) ||
                subj.toLowerCase().contains(search) ||
                text.toLowerCase().contains(search) ||
                title.toLowerCase().contains(search);
    }
}
