package com.phantomvk.slideback.demo;

import android.os.Build;
import android.view.MenuItem;

import com.phantomvk.slideback.SlideLayout;
import com.phantomvk.slideback.SlideManager;

public class MainActivity extends BaseActivity implements SlideManager.Conductor {

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        SlideLayout l = slideManager.getSlideLayout();
        if (l != null) {
            l.setScrimInterpolation((context, slidePercent) -> 0);
            l.setShadowInterpolation((context, slidePercent) -> 1 - slidePercent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finishAfterTransition() {
        SlideLayout l = slideManager.getSlideLayout();
        if (l != null) {
            l.slideExit();
        } else {
            super.finishAfterTransition();
        }
    }

    /**
     * Available since Android 4.4(API19).
     */
    @Override
    public boolean slideBackDisable() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }
}
