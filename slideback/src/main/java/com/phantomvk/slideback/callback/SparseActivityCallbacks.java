package com.phantomvk.slideback.callback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.SparseArray;

import com.phantomvk.slideback.utility.TranslucentHelper;

/**
 * For Standard Activity Stack only.
 */
public class SparseActivityCallbacks implements Application.ActivityLifecycleCallbacks {

    private SparseArray<Activity> running = new SparseArray<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        running.put(running.size(), activity);
        TranslucentHelper.setTranslucent(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (!activity.isFinishing() && running.size() - running.indexOfValue(activity) == 2) {
            TranslucentHelper.setTranslucent(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (!activity.isFinishing() && running.size() - running.indexOfValue(activity) == 1) {
            TranslucentHelper.removeTranslucent(activity);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        running.remove(running.indexOfValue(activity));
    }
}
