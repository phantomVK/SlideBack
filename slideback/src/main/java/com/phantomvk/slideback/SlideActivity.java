package com.phantomvk.slideback;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SlideActivity extends AppCompatActivity {

    protected SlideManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SlideManager.setWindowBackground(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mManager = new SlideManager(this);
    }

    @Override
    public <T extends View> T findViewById(int id) {
        SlideLayout l = mManager.getSlideLayout();
        return (T) (l != null ? l.findViewById(id) : super.findViewById(id));
    }
}
