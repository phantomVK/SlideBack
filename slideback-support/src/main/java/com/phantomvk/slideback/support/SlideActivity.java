package com.phantomvk.slideback.support;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * The class for subclass to extend.
 * If {@link AppCompatActivity} ths class extended is not satisfied with the actual requirements,
 * please create a new class to extend what you are willing to use, then do implement the same as
 * what this class do.
 */
public class SlideActivity extends AppCompatActivity {

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
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        slideManager.startActivityForResult(intent, requestCode, options);
    }
}
