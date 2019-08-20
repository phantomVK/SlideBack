package com.phantomvk.slideback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.phantomvk.slideback.utility.TranslucentHelper;

public class ActivityCallbacks implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity.isFinishing() || !((activity instanceof SlideManager.Conductor))) return;
        SlideManager.Conductor c = (SlideManager.Conductor) activity;
        if (!c.slideBackDisable()) TranslucentHelper.setTranslucent(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
