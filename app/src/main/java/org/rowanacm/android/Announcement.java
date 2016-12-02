package org.rowanacm.android;

import java.io.Serializable;

/**
 * Created by John on 12/1/2016.
 */

public class Announcement implements Serializable {
    private String committee = "";
    private String announcement = "";

    public String getCommittee() {
        return committee;
    }

    public void setCommittee(String committee) {
        this.committee = committee;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }
}
