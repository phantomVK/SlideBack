package com.phantomvk.slideback.demo.union;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;

import com.phantomvk.slideback.listener.SlideStateListener;

/**
 * The demonstrate to portrait screen and slide from left side.
 */
public class CustomAdapter implements SlideStateListener {
    private static final float SCALE = 0.283F;

    private final float translationX;
    private final Activity activity;
    private View decor;

    public CustomAdapter(Activity activity) {
        this.activity = activity;

        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        translationX = SCALE * point.x;
    }

    @Override
    public void onEdgeTouched(int edgeFlags) {
        if (((UnionSlideMonitor) activity).isUnionSlideEnable()) {
            decor = ActivityStack.peek();
        }
    }

    @Override
    public void onDragStateChanged(int state, float scrollPercent) {
        if (decor == null) return;

        if (scrollPercent == 0.0) {
            decor.setTranslationX(0);
            decor = null;
        } else {
            decor.setTranslationX((float) (Math.sqrt(scrollPercent) - 1) * translationX);
        }
    }

    @Override
    public void onOutOfRange() {
        if (activity.isFinishing() || (SDK_INT >= JELLY_BEAN_MR1 && activity.isDestroyed())) return;

        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    @Override
    public void onSlideOverThreshold() {
    }

    @FunctionalInterface
    public interface UnionSlideMonitor {
        boolean isUnionSlideEnable();
    }
}
