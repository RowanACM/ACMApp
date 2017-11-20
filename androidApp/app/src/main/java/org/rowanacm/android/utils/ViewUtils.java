package org.rowanacm.android.utils;

import android.support.design.widget.FloatingActionButton;
import android.view.View;

public class ViewUtils {

    public static void setVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public static void setVisibility(FloatingActionButton fab, boolean show) {
        if (show) {
            fab.show();
        } else {
            fab.hide();
        }
    }
}
