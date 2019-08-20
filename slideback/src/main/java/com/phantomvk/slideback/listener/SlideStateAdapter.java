package com.phantomvk.slideback.listener;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;

public class SlideStateAdapter extends BaseSlideStateAdapter {

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
        finishActivity(activity);
    }

    /**
     * Finish an activity with full slide back animation, mainly used by
     * {@link SlideStateListener#onSlideOverRange()}.
     *
     * @param activity the target activity to finish
     */
    public void finishActivity(@Nullable Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        if (SDK_INT >= JELLY_BEAN_MR1 && activity.isDestroyed()) return;

        activity.finish();
        activity.overridePendingTransition(0, 0);
    }
}
