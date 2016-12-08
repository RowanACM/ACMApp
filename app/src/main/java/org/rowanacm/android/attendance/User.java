package org.rowanacm.android.attendance;

/**
 * Created by tyler on 12/7/16.
 */

public class User {
    private String name;
    private String rowanEmailAddress;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRowanEmailAddress() {
        return rowanEmailAddress;
    }

    public void setRowanEmailAddress(String rowanEmailAddress) {
        this.rowanEmailAddress = rowanEmailAddress;
    }

    public User(String name, String rowanEmailAddress) {

        this.name = name;
        this.rowanEmailAddress = rowanEmailAddress;
    }

    public boolean isValid() {


        return name != null && (!name.isEmpty()) &&
                rowanEmailAddress != null && (!rowanEmailAddress.isEmpty());
    }
}
