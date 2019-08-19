package com.phantomvk.slideback.demo;

import android.os.Bundle;

import com.phantomvk.slideback.SlideManager;

public class MainActivity extends BaseActivity implements SlideManager.Conductor {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean slideBackDisable() {
        return false;
    }
}
