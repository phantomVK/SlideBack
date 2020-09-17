package com.phantomvk.slideback.support.listener;

import android.app.Activity;
import android.support.annotation.NonNull;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;

public class SlideStateAdapter extends SlideStateListener {
    /**
     * The target activity to control.
     */
    protected final Activity activity;

    /**
     * Constructor.
     *
     * @param activity the target activity to control
     */
    public SlideStateAdapter(@NonNull Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onSlideOverRange() {
        if (activity.isFinishing() || (SDK_INT >= JELLY_BEAN_MR1 && activity.isDestroyed())) return;

        activity.finish();
        activity.overridePendingTransition(0, 0);
    }
}
