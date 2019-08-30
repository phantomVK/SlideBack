package com.phantomvk.slideback.demo;

import android.os.Bundle;

import com.phantomvk.slideback.SlideLayout;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, 0);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        SlideLayout l = mManager.getSlideLayout();
        if (l != null) {
            l.setScrimInterpolation((context, slidePercent) -> 0);
            l.setShadowInterpolation((context, slidePercent) -> 1);
        }
    }

    @Override
    public void finishAfterTransition() {
        SlideLayout l = mManager.getSlideLayout();
        if (l != null) {
            l.slideExit();
        } else {
            super.finishAfterTransition();
        }
    }
}
