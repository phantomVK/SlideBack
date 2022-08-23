package com.phantomvk.slideback;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The class for subclass to extend.
 * <p>
 * If ths extended super class {@link AppCompatActivity} is not satisfied with the actual
 * requirements, please create a new class to extend what you are willing to use, then do implement
 * the same as what this class do.
 * <p>
 * Also, Do not forget to implement interface named {@link SlideManager.Conductor} if method
 * {@link SlideManager.Conductor#slideBackDisable()} is needed.
 */
public class SlideActivity extends AppCompatActivity {

    @Nullable
    protected SlideManager slideManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slideManager = slideManagerProvider();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        if (slideManager != null) {
            slideManager.onContentChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (slideManager != null) {
            slideManager.onResume();
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if (slideManager != null) {
            slideManager.startActivityForResult(intent, requestCode, options);
        }
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        if (slideManager != null) {
            slideManager.onEnterAnimationComplete();
        }
    }

    /**
     * Provide a Nullable custom {@link SlideManager}.
     *
     * @return a SlideManager instance.
     */
    @Nullable
    protected SlideManager slideManagerProvider() {
        return new SlideManager(this);
    }
}
