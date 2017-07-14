package org.rowanacm.android;

public class AttendanceResult {

    private String message;
    private String status;
    private int response_code;

    public AttendanceResult() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getResponseCode() {
        return response_code;
    }

    public void setResponseCode(int response_code) {
        this.response_code = response_code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttendanceResult that = (AttendanceResult) o;

        if (response_code != that.response_code) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        return status != null ? status.equals(that.status) : that.status == null;

    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + response_code;
        return result;
    }

    @Override
    public String toString() {
        return "AttendanceResult{" +
                "message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", response_code=" + response_code +
                '}';
    }
}
