package com.phantomvk.slideback.demo.union;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.phantomvk.slideback.SlideActivity;
import com.phantomvk.slideback.SlideManager;
import com.phantomvk.slideback.demo.R;

import static androidx.customview.widget.ViewDragHelper.EDGE_LEFT;

public class BaseActivity extends SlideActivity implements CustomAdapter.UnionMonitor {

    // For demo only.
    protected int trackingEdge = EDGE_LEFT;
    private boolean mEnterAnimationComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.push(this);

        if (!slideManager.isSlideDisable()) {
            overridePendingTransition(R.anim.slide_in_right, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.pop();
    }

    @Override
    public void finish() {
        super.finish();
        if (!slideManager.isSlideDisable()) {
            overridePendingTransition(0, R.anim.slide_out_right);
        }
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        mEnterAnimationComplete = true;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        // No new activity is allowed to start before this activity's enter animation is completed.
        // This flag is largely improve the user experience.
        if (!mEnterAnimationComplete) return;
        super.startActivityForResult(intent, requestCode, options);
    }

    @NonNull
    @Override
    protected SlideManager slideManagerProvider() {
        return new SlideManager(this, new CustomAdapter(this));
    }

    @Override
    public boolean isUnionEnable() {
        return trackingEdge == EDGE_LEFT;
    }
}
