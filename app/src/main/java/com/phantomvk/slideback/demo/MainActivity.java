package com.phantomvk.slideback.demo;

import android.view.MenuItem;

import com.phantomvk.slideback.SlideLayout;

public class MainActivity extends BaseActivity {

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
}
