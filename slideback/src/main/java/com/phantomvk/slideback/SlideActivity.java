package com.phantomvk.slideback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SlideActivity extends AppCompatActivity implements SlideManager.Conductor {

    protected SlideManager mManager;
    protected boolean isTranslucent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SlideManager.setWindowBackground(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mManager = new SlideManager(this);
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
        super.startActivityForResult(intent, requestCode, options);
        mManager.startActivityForResult(intent, requestCode, options);
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
