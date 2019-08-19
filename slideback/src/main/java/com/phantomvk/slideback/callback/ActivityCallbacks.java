package com.phantomvk.slideback.callback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.phantomvk.slideback.utility.TranslucentHelper;

import java.util.HashMap;

/**
 * For Standard Activity Stack only.
 */
public class ActivityCallbacks implements Application.ActivityLifecycleCallbacks {

    private HashMap<Activity, Integer> running = new HashMap<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        running.put(activity, running.size());
        TranslucentHelper.setTranslucent(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (!activity.isFinishing()) {
            Integer i = running.get(activity);
            if (i != null && running.size() - i == 2) {
                TranslucentHelper.setTranslucent(activity);
            }
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (!activity.isFinishing()) {
            Integer i = running.get(activity);
            if (i != null && running.size() - i == 1) {
                TranslucentHelper.removeTranslucent(activity);
            }
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
        running.remove(activity);
    }
}
