package com.phantomvk.slideback.demo.union;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;

import com.phantomvk.slideback.listener.SlideStateListener;

import java.lang.ref.WeakReference;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;

/**
 * The demonstrate to portrait screen and slide from left side.
 */
public class SlideAdapter implements SlideStateListener {
    private static final Point POINT = new Point();
    private static final float SCALE = 0.283F;

    private Activity activity;
    private View decorView;
    private float translationX;

    public SlideAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onEdgeTouched(int edgeFlags) {
        activity.getWindowManager().getDefaultDisplay().getSize(POINT);
        translationX = SCALE * POINT.x;

        WeakReference<View> ref = ActivityStack.peek();
        if (ref != null && ((UnionMonitor) activity).isUnionEnable()) {
            decorView = ref.get();
        }
    }

    @Override
    public void onDragStateChanged(int state, float scrollPercent) {
        if (decorView == null) return;

        if (scrollPercent == 0.0) {
            decorView.setTranslationX(0);
            decorView = null;
        } else {
            decorView.setTranslationX((scrollPercent - 1) * translationX);
        }
    }

    @Override
    public void onSlideOverRange() {
        if (activity.isFinishing() || (SDK_INT >= JELLY_BEAN_MR1 && activity.isDestroyed())) return;

        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    @Override
    public void onSlideOverThreshold() {
    }

    @FunctionalInterface
    public interface UnionMonitor {
        boolean isUnionEnable();
    }
}
