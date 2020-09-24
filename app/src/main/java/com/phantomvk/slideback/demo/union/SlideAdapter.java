package com.phantomvk.slideback.demo.union;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;

import com.phantomvk.slideback.listener.SlideStateListener;
import com.phantomvk.slideback.utility.ViewDragHelper;

import java.lang.ref.WeakReference;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;

/**
 * The demonstrate to portrait screen.
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
        if (ref != null) {
            decorView = ref.get();
        }
    }

    @Override
    public void onDragStateChanged(int state, float scrollPercent) {
        if (decorView == null) return;
        if (state == ViewDragHelper.STATE_IDLE) {
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
}
