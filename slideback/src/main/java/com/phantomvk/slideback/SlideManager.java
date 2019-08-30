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
     * To indicate the states of the current activity.
     */
    protected Conductor conductor;

    /**
     * Mark the latest translucent state of the current activity.
     */
    protected boolean isTranslucent;

    /**
     * Constructor which using a default {@link SlideStateListener} implementation
     * named {@link SlideStateAdapter} to finish the current activity.
     *
     * @param activity must not be null and implements {@link SlideManager.Conductor}
     */
    public SlideManager(@NonNull Activity activity) {
        this(activity, null);
    }

    /**
     * Constructor for using a custom {@link SlideStateListener}.
     *
     * @param activity must not be null and implements {@link SlideManager.Conductor}
     * @param listener default implementation will be used if this is null
     */
    public SlideManager(@NonNull Activity activity, @Nullable SlideStateListener listener) {
        Conductor c = (activity instanceof Conductor) ? (Conductor) activity : null;
        if (c != null && c.slideBackDisable()) return;

        this.conductor = c;
        this.activity = activity;
        listener = (listener == null) ? new SlideStateAdapter(activity) : listener;

        slideLayout = new SlideLayout(activity);
        slideLayout.setTrackingEdge(ViewDragHelper.EDGE_LEFT);
        slideLayout.addListener(listener);
    }

    /**
     * Called on Activity.onCreate(Bundle)
     */
    public void onCreate(Bundle savedInstanceState) {
        if (activity == null) return;
        activity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        activity.getWindow().getDecorView().setBackgroundDrawable(null);
    }

    /**
     * Called on Activity.onContentChanged()
     */
    public void onContentChanged() {
        if (slideLayout == null) return;
        slideLayout.attach(activity);
    }

    /**
     * Called on Activity.onResume()
     */
    public void onResume() {
        if ((conductor != null && conductor.slideBackDisable()) || isTranslucent) return;
        TranslucentHelper.setTranslucent(activity);
        isTranslucent = true;
    }

    /**
     * Called on Activity.startActivityForResult(Intent, int, Bundle)
     */
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        if ((conductor != null && conductor.slideBackDisable()) || !isTranslucent) return;
        TranslucentHelper.removeTranslucent(activity);
        isTranslucent = false;
    }

    /**
     * Return the instance of {@link SlideLayout}, maybe null or caused NullPointerException
     * when the value returned from {@link Conductor#slideBackDisable()} is true.
     *
     * @return the instance of SlideLayout, returned null pointer or caused NullPointerException
     */
    @Nullable
    public SlideLayout getSlideLayout() {
        return slideLayout;
    }

    /**
     * Indicate whether the current activity is translucent.
     *
     * @return is translucent
     */
    public boolean isTranslucent() {
        return isTranslucent;
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
         * Indicate whether SlideBack is totally disabled.
         */
        boolean slideBackDisable();
    }
}
