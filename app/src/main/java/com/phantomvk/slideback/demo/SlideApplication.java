package com.phantomvk.slideback.demo;

public class SlideApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityCallBack());
    }
}
