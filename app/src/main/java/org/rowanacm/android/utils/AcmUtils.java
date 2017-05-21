package org.rowanacm.android.utils;


import android.support.design.widget.FloatingActionButton;

public class AcmUtils {

    public static void setFabVisibility(FloatingActionButton fab, boolean show) {
        if (show) {
            fab.show();
        } else {
            fab.hide();
        }
    }
}
