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

    private final float scaleX;
    private final Activity activity;
    private View prevDecorView;

    public CustomAdapter(Activity activity) {
        this.activity = activity;

        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        scaleX = SCALE * point.x;
    }

    @Override
    public void onEdgeTouched(int edgeFlags) {
        if (((UnionSlideMonitor) activity).isUnionSlideEnable()) {
            Activity prevActivity = ActivityStack.peek();
            prevDecorView = (prevActivity == null) ? null : prevActivity.getWindow().getDecorView();
        }
    }

    @Override
    public void onDragStateChanged(int state, float scrollPercent) {
        if (prevDecorView == null) {
            return;
        }

        if (scrollPercent == 0.0) {
            prevDecorView.setTranslationX(0);
            prevDecorView = null;
            return;
        }

        float translationX = ((float) (Math.sqrt(scrollPercent) - 1)) * scaleX;
        prevDecorView.setTranslationX(translationX);
    }

    @Override
    public void onOutOfRange() {
        if (activity.isFinishing()) {
            return;
        }

        if (SDK_INT >= JELLY_BEAN_MR1 && activity.isDestroyed()) {
            return;
        }

        activity.finish();
    }

    @Override
    public void onSlideOverThreshold() {
    }

    @FunctionalInterface
    public interface UnionSlideMonitor {
        boolean isUnionSlideEnable();
    }
}
