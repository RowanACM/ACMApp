package org.rowanacm.android;


import com.google.firebase.database.FirebaseDatabase;

public class AdminManager {

    public void setAttendanceEnabled(boolean enabled) {
        FirebaseDatabase.getInstance().getReference("attendance")
                .child("status").child("enabled").setValue(enabled);
    }


}
