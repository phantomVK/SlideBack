package com.phantomvk.slideback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.phantomvk.slideback.utility.TranslucentHelper;

public class SlideActivity extends AppCompatActivity implements SlideManager.Conductor {

    protected SlideManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SlideManager.setWindowBackground(this);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mManager = new SlideManager(this);
    }

    @Override
    public <T extends View> T findViewById(int id) {
        SlideLayout l = mManager.getSlideLayout();
        return (T) (l != null ? l.findViewById(id) : super.findViewById(id));
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        if (!this.slideBackDisable()) TranslucentHelper.removeTranslucent(this);
    }

    @Override
    public boolean slideBackDisable() {
        return false;
    }
}
