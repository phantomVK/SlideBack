package com.phantomvk.slideback.support;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.phantomvk.slideback.support.listener.SlideStateAdapter;
import com.phantomvk.slideback.support.listener.SlideStateListener;

public class SlideManager {

    private static final ColorDrawable DRAWABLE_TRANSPARENT = new ColorDrawable(Color.TRANSPARENT);

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
        conductor = (activity instanceof Conductor) ? (Conductor) activity : null;
        if (isSlideDisable()) return;

        this.activity = activity;

        slideLayout = new SlideLayout(activity);
        slideLayout.addListener((listener == null) ? new SlideStateAdapter(activity) : listener);

        activity.getWindow().setBackgroundDrawable(DRAWABLE_TRANSPARENT);
    }

    /**
     * Called on Activity.onContentChanged() with background from theme.
     */
    public void onContentChanged() {
        if (!isSlideDisable()) slideLayout.attach(activity);
    }

    /**
     * Called on Activity.onContentChanged() with background color int.
     */
    public void onContentChanged(@ColorInt int color) {
        if (!isSlideDisable()) slideLayout.attachColor(activity, color);
    }

    /**
     * Called on Activity.onContentChanged() with background color resource.
     */
    public void onContentChangedRes(@DrawableRes int colorRes) {
        if (!isSlideDisable()) slideLayout.attachColorRes(activity, colorRes);
    }

    /**
     * Called on Activity.onResume()
     */
    public void onResume() {
        if (!isSlideDisable() && !slideLayout.isDrawComplete()) {
            slideLayout.convertToTranslucent();
        }
    }

    /**
     * Called on Activity.onPause()
     */
    public void onPause() {
        if (!isSlideDisable() && !activity.isFinishing()) slideLayout.convertFromTranslucent();
    }

    /**
     * Activities cannot draw during the period that their windows are animating in. In order
     * to know when it is safe to begin drawing they can override this method which will be
     * called when the entering animation has completed.
     * <p>
     * For more details, see onEnterAnimationComplete() in {@link Activity}.
     */
    public void onEnterAnimationComplete() {
        slideLayout.onEnterAnimationComplete();
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

    protected boolean isSlideDisable() {
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
