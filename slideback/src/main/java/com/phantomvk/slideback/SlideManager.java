package com.phantomvk.slideback;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SlideManager {
    /**
     * Activity.
     */
    protected Activity activity;

    /**
     * SlideLayout.
     */
    protected SlideLayout slideLayout;

    /**
     * Default constructor, use default SlideStateListener to finish activity.
     *
     * @param activity Activity
     */
    public SlideManager(@NonNull Activity activity) {
        this(activity, null);
    }

    private Conductor conductor;

    /**
     * Constructor, default SlideStateListener will be used if listener is null.
     *
     * @param activity Activity
     * @param listener SlideStateListener
     */
    public SlideManager(@NonNull Activity activity, @Nullable SlideStateListener listener) {
        Conductor c = (activity instanceof Conductor) ? (Conductor) activity : null;
        if (c != null && c.slideBackDisable()) return;

        this.activity = activity;
        this.conductor = c;
        listener = (listener == null) ? new SlideStateAdapter(activity) : listener;

        slideLayout = new SlideLayout(activity);
        slideLayout.setTrackingEdge(ViewDragHelper.EDGE_LEFT);
        slideLayout.addListener(listener);
    }

    public void onPostCreate() {
        if (conductor != null && conductor.slideBackDisable()) return;
        slideLayout.attach(activity);
        TranslucentHelper.setTranslucent(activity);
    }

    @Nullable
    public SlideLayout getSlideLayout() {
        return slideLayout;
    }

    public interface Conductor {
        boolean slideBackDisable();
    }

    public static void setWindowBackground(@Nullable Activity activity) {
        if (activity == null) return;
        activity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        activity.getWindow().getDecorView().setBackgroundDrawable(null);
    }
}
