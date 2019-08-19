package com.phantomvk.slideback.demo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.phantomvk.slideback.TranslucentHelper;

import java.util.HashMap;

public class ActivityCallBack implements Application.ActivityLifecycleCallbacks {

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
        if (!activity.isFinishing() && running.size() - running.get(activity) == 2) {
            TranslucentHelper.setTranslucent(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (!activity.isFinishing() && running.size() - running.get(activity) == 1) {
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
        running.remove(activity);
    }
}
