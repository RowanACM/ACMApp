package org.rowanacm.android.annoucement;

import java.io.Serializable;

/**
 * An message by Rowan ACM.
 * Contains a message and committee
 */

public class Announcement implements Serializable {
    private String author;
    private String committee;
    private String date;
    private String subject;
    private String text;
    private String title;
    private long timestamp;

    public Announcement() {
    }

    public Announcement(String author, String committee, String date, String subject, String text, String title, long timestamp) {
        this.author = author;
        this.committee = committee;
        this.date = date;
        this.subject = subject;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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
}
