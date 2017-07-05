package org.rowanacm.android.utils;

import android.view.View;

public class ViewUtils {

    public static void setVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
