package org.rowanacm.android.firebase;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import timber.log.Timber;

public class FirebaseCrashTree extends Timber.Tree {

    @Override
    protected void log(int priority, String tag, String message, @Nullable Throwable t) {
        if (priority == Log.WARN || priority == Log.ERROR || priority == Log.ASSERT) {
            FirebaseCrash.logcat(priority, tag, message);

            if (t != null) {
                FirebaseCrash.report(t);
            }
        }
    }

}
