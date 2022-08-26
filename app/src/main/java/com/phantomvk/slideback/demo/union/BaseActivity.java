package com.phantomvk.slideback.demo.union;

import static androidx.customview.widget.ViewDragHelper.EDGE_LEFT;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.phantomvk.slideback.SlideActivity;
import com.phantomvk.slideback.SlideManager;

public class BaseActivity extends SlideActivity implements CustomAdapter.UnionSlideMonitor {

    protected int trackingEdge = EDGE_LEFT;
    private boolean mEnterAnimationComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.push(this);
    }

    @Override
    public void finish() {
        ActivityStack.pop();
        super.finish();
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        mEnterAnimationComplete = true;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if (mEnterAnimationComplete) {
            super.startActivityForResult(intent, requestCode, options);
        }
    }

    @Nullable
    @Override
    protected SlideManager slideManagerProvider() {
        return new SlideManager(this, new CustomAdapter(this));
    }

    @Override
    public boolean isUnionSlideEnable() {
        return trackingEdge == EDGE_LEFT;
    }
}
