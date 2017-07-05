package org.rowanacm.android;

import java.util.Calendar;

public class CalendarItem {

    private String title;
    private String description;
    private String location;
    private String repeatRule;
    private Calendar startTime;
    private int lengthMinutes;

    public CalendarItem(String title, String description, String location, String repeatRule, Calendar startTime, int lengthMinutes) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.repeatRule = repeatRule;
        this.startTime = startTime;
        this.lengthMinutes = lengthMinutes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRepeatRule() {
        return repeatRule;
    }

    public void setRepeatRule(String repeatRule) {
        this.repeatRule = repeatRule;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public int getLengthMinutes() {
        return lengthMinutes;
    }

    public void setLengthMinutes(int lengthMinutes) {
        this.lengthMinutes = lengthMinutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarItem that = (CalendarItem) o;

        if (lengthMinutes != that.lengthMinutes) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (location != null ? !location.equals(that.location) : that.location != null)
            return false;
        if (repeatRule != null ? !repeatRule.equals(that.repeatRule) : that.repeatRule != null)
            return false;
        return startTime != null ? startTime.equals(that.startTime) : that.startTime == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (repeatRule != null ? repeatRule.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + lengthMinutes;
        return result;
    }

    @Override
    public String toString() {
        return "CalendarItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", repeatRule='" + repeatRule + '\'' +
                ", startTime=" + startTime +
                ", lengthMinutes=" + lengthMinutes +
                '}';
    }
}
