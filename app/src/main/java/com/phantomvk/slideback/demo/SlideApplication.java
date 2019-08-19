package com.phantomvk.slideback.demo;

import com.phantomvk.slideback.callback.ActivityCallbacks;

public class SlideApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityCallbacks());
    }
}
