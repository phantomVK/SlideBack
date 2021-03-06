package com.phantomvk.slideback.support;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.phantomvk.slideback.support.listener.SlideStateAdapter;
import com.phantomvk.slideback.support.listener.SlideStateListener;

public class SlideManager {

    private static final ColorDrawable DRAWABLE_TRANSPARENT = new ColorDrawable(Color.TRANSPARENT);
    private static final Conductor CONDUCTOR = () -> Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;

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
     * Constructor to use default configuration.
     *
     * @param activity must not be null
     */
    public SlideManager(@NonNull Activity activity) {
        this(activity,
                activity instanceof Conductor ? (Conductor) activity : CONDUCTOR,
                new SlideStateAdapter(activity));
    }

    /**
     * Constructor to use a custom {@link Conductor}.
     *
     * @param activity  must not be null
     * @param conductor must not be null. For more detail, see {@link Conductor}
     */
    public SlideManager(@NonNull Activity activity, @NonNull Conductor conductor) {
        this(activity, conductor, new SlideStateAdapter(activity));
    }

    /**
     * Constructor to use a custom {@link SlideStateListener}.
     *
     * @param activity must not be null
     * @param listener must not be null. For more detail, see {@link SlideStateListener}
     */
    public SlideManager(@NonNull Activity activity, @NonNull SlideStateListener listener) {
        this(activity, activity instanceof Conductor ? (Conductor) activity : CONDUCTOR, listener);
    }

    /**
     * Constructor to use a {@link Conductor} and a custom {@link SlideStateListener}.
     *
     * @param activity  must not be null
     * @param conductor must not be null. For more detail, see {@link Conductor}
     * @param listener  must not be null. For more detail, see {@link SlideStateListener}
     */
    public SlideManager(@NonNull Activity activity,
                        @NonNull Conductor conductor,
                        @NonNull SlideStateListener listener) {

        this.conductor = conductor;
        if (isSlideDisable()) return;

        this.activity = activity;

        slideLayout = new SlideLayout(activity);
        slideLayout.addListener(listener);

        activity.getWindow().setBackgroundDrawable(DRAWABLE_TRANSPARENT);
    }

    /**
     * Called in {@link Activity#onContentChanged()} with background from theme.
     */
    public void onContentChanged() {
        if (!isSlideDisable()) slideLayout.attach(activity);
    }

    /**
     * Called in {@link Activity#onContentChanged()} with background color int.
     */
    public void onContentChanged(@ColorInt int color) {
        if (!isSlideDisable()) slideLayout.attachColor(activity, color);
    }

    /**
     * Called in {@link Activity#onContentChanged()} with background color resource.
     */
    public void onContentChangedRes(@DrawableRes int colorRes) {
        if (!isSlideDisable()) slideLayout.attachColorRes(activity, colorRes);
    }

    /**
     * Called in Activity.onResume().
     */
    public void onResume() {
        if (!isSlideDisable() && !slideLayout.isDrawComplete()) slideLayout.convertToTranslucent();
    }

    /**
     * Called in Activity.startActivityForResult().
     */
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if (!isSlideDisable() && !activity.isFinishing()) slideLayout.convertFromTranslucent();
    }

    /**
     * Activities cannot draw during the period that their windows are animating in. In order
     * to know when it is safe to begin drawing they can override this method which will be
     * called when the entering animation has completed.
     * <p>
     * For more details, see {@link Activity#onEnterAnimationComplete()}.
     */
    public void onEnterAnimationComplete() {
        if (!isSlideDisable()) slideLayout.onEnterAnimationComplete();
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
        return !isSlideDisable() && slideLayout.isDrawComplete();
    }

    public boolean isSlideDisable() {
        return conductor != null && conductor.slideBackDisable();
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
