package com.phantomvk.slideback.support;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


/**
 * The class for subclass to extend.
 * If {@link AppCompatActivity} ths class extended is not satisfied with the actual requirements,
 * please create a new class to extend what you are willing to use, then do implement the same as
 * what this class do.
 */
public class SlideActivity extends AppCompatActivity {

    protected SlideManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager = new SlideManager(this);
        mManager.onCreate(savedInstanceState);
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
}