package org.rowanacm.android;

import java.io.Serializable;

/**
 * An message by Rowan ACM.
 * Contains a message and committee
 */

public class Announcement implements Serializable {
    private String committee = "";
    private String message = "";

    public Announcement() {
    }

    public Announcement(String committee, String message) {
        this.committee = committee;
        this.message = message;
    }

    public String getCommittee() {
        return committee;
    }

    public void setCommittee(String committee) {
        this.committee = committee;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
