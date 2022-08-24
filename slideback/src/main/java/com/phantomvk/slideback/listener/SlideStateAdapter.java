package com.phantomvk.slideback.listener;

import android.app.Activity;

import androidx.annotation.NonNull;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;

public class SlideStateAdapter implements SlideStateListener {
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
    public void onOutOfRange() {
        if (activity.isFinishing() || (SDK_INT >= JELLY_BEAN_MR1 && activity.isDestroyed())) return;

        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    @Override
    public void onEdgeTouched(int edgeFlags) {
    }

    @Override
    public void onDragStateChanged(int state, float scrollPercent) {
    }

    @Override
    public void onSlideOverThreshold() {
    }
}
