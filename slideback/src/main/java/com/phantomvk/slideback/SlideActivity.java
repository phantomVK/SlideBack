package com.phantomvk.slideback;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The class for subclass to extend.
 * If {@link AppCompatActivity} ths class extended is not satisfied with the actual requirements,
 * please create a new class to extend what you are willing to use, then do implement the same as
 * what this class do.
 */
public class SlideActivity extends AppCompatActivity implements SlideManager.Conductor {

    protected SlideManager slideManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slideManager = new SlideManager(this);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        slideManager.onContentChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        slideManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        slideManager.onPause();
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        slideManager.onEnterAnimationComplete();
    }

    @Override
    public boolean slideBackDisable() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }
}
