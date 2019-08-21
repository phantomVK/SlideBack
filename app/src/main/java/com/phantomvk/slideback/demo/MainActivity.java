package com.phantomvk.slideback.demo;

import android.os.Bundle;

import com.phantomvk.slideback.SlideLayout;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onBackPressed() {
        SlideLayout m = mManager.getSlideLayout();
        if (m != null) {
            m.slideExit();
        } else {
            super.onBackPressed();
        }
    }
}
