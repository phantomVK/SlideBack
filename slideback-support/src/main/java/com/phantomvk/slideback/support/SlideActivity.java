package com.phantomvk.slideback.support;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * The class for subclass to extend.
 * <p>
 * If {@link AppCompatActivity} ths class extended is not satisfied with the actual requirements,
 * please create a new class to extend what you are willing to use, then do implement the same as
 * what this class do.
 * <p>
 * Also, Do not forget to implement interface named {@link SlideManager.Conductor} if method
 * {@link SlideActivity#slideBackDisable()} is needed.
 */
public class SlideActivity extends AppCompatActivity implements SlideManager.Conductor {

    protected SlideManager slideManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slideManager = slideManagerProvider();
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
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        slideManager.startActivityForResult(intent, requestCode, options);
        super.startActivityForResult(intent, requestCode, options);
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

    /**
     * Provide a NonNull custom {@link SlideManager} instance by subclass.
     *
     * @return a SlideManager instance.
     */
    protected SlideManager slideManagerProvider() {
        return new SlideManager(this);
    }
}
