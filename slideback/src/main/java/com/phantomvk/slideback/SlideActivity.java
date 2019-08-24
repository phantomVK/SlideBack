package com.phantomvk.slideback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The class for subclass to extend.
 * If {@link AppCompatActivity} ths class extended is not satisfied with the actual requirements,
 * please create a new class to extend what you are willing to use, then do implement the same as
 * what this class do.
 */
public class SlideActivity extends AppCompatActivity implements SlideManager.Conductor {

    protected SlideManager mManager;
    protected boolean isTranslucent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager = new SlideManager(this);
        mManager.onCreate();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mManager.onContentChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mManager.onResume();
    }

    @Override
    public <T extends View> T findViewById(int id) {
        SlideLayout l = mManager.getSlideLayout();
        return (T) (l != null ? l.findViewById(id) : super.findViewById(id));
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        mManager.startActivityForResult(intent, requestCode, options);
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public boolean slideBackDisable() {
        return false;
    }

    @Override
    public boolean isTranslucent() {
        return isTranslucent;
    }

    @Override
    public void markTranslucent(boolean translucent) {
        isTranslucent = translucent;
    }
}
