package com.phantomvk.slideback;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.phantomvk.slideback.listener.SlideStateAdapter;
import com.phantomvk.slideback.listener.SlideStateListener;
import com.phantomvk.slideback.utility.TranslucentHelper;
import com.phantomvk.slideback.utility.ViewDragHelper;

public class SlideManager {
    /**
     * The target activity to control.
     */
    protected Activity activity;

    /**
     * SlideLayout.
     */
    protected SlideLayout slideLayout;

    /**
     * Conductor.
     */
    protected Conductor conductor;

    /**
     * Default constructor, use default SlideStateListener to finish activity.
     *
     * @param activity Activity
     */
    public SlideManager(@NonNull Activity activity) {
        this(activity, null);
    }

    /**
     * Constructor, default SlideStateListener will be used if listener is null.
     *
     * @param activity Activity
     * @param listener SlideStateListener
     */
    public SlideManager(@Nullable Activity activity, @Nullable SlideStateListener listener) {
        Conductor c = (activity instanceof Conductor) ? (Conductor) activity : null;
        if (c == null) {
            throw new IllegalArgumentException(
                    "Activity must implement interface SlideManager::Conductor.");
        } else if (c.slideBackDisable()) {
            return;
        }

        this.conductor = c;
        this.activity = activity;
        listener = (listener == null) ? new SlideStateAdapter(activity) : listener;

        slideLayout = new SlideLayout(activity);
        slideLayout.setTrackingEdge(ViewDragHelper.EDGE_LEFT);
        slideLayout.addListener(listener);
        slideLayout.attach(activity);
    }

    public void onResume() {
        if (sanityCheck()) return;
        TranslucentHelper.setTranslucent(activity);
        conductor.markTranslucent(true);
    }

    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        if (sanityCheck()) return;
        TranslucentHelper.removeTranslucent(activity);
        conductor.markTranslucent(false);
    }

    private boolean sanityCheck() {
        return activity == null || conductor == null
                || conductor.slideBackDisable()
                || !conductor.isTranslucent();
    }

    /**
     * Return the instance of {@link SlideLayout}, maybe null or caused NullPointerException
     * when the value returned from {@link Conductor#slideBackDisable()} is true.
     *
     * @return the instance of SlideLayout, null pointer or caused NullPointerException
     */
    @Nullable
    public SlideLayout getSlideLayout() {
        return slideLayout;
    }

    /**
     * Used to totally disable SlideLayout.
     * <p>
     * Usage: implemented {@link Conductor} by the subclass of Activity which is using, then
     * override method {@link Conductor#slideBackDisable()} to return true.
     * <p>
     * As a result, this class will not be initialized, any result returned from its methods
     * is null or just caused NullPointerException.
     * <p>
     * Re-initializing is illegal even changing the value returned from {@link #slideBackDisable()}
     * from true to false.
     * <p>
     * If just temporarily disable sliding, use {@link SlideLayout#setEnable(boolean)} of the
     * instance which returned from {@link SlideManager#getSlideLayout()}, and do not need to
     * implement {@link Conductor#slideBackDisable()}, or implement but return false permanently.
     */
    public interface Conductor {
        /**
         * Indicate if SlideBack is totally disabled.
         *
         * @return is disable
         */
        boolean slideBackDisable();

        /**
         * Indicate current activity is translucent.
         *
         * @return is translucent
         */
        boolean isTranslucent();

        /**
         * Mark the translucent state of activity.
         */
        void markTranslucent(boolean translucent);
    }

    public static void setWindowBackground(@Nullable Activity activity) {
        if (activity == null) return;
        activity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        activity.getWindow().getDecorView().setBackgroundDrawable(null);
    }
}
