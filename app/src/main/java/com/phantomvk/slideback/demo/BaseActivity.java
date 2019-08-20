package com.phantomvk.slideback.demo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.customview.widget.ViewDragHelper;

import com.phantomvk.slideback.SlideActivity;
import com.phantomvk.slideback.SlideLayout;

import static androidx.customview.widget.ViewDragHelper.EDGE_BOTTOM;
import static androidx.customview.widget.ViewDragHelper.EDGE_LEFT;
import static androidx.customview.widget.ViewDragHelper.EDGE_RIGHT;
import static androidx.customview.widget.ViewDragHelper.EDGE_TOP;

@SuppressLint("Registered")
public class BaseActivity extends SlideActivity {

    private static int sIndex = 0;
    private static boolean sInit = false;

    /**
     * {@link ViewDragHelper#EDGE_TOP} Required using 'Theme.AppCompat.Light.NoActionBar'
     * as the theme of activity, rather then 'Theme.AppCompat.Light.DarkActionBar' or any
     * other themes contain ActionBar, to avoid ActionBar consuming touch events when user
     * is sliding view.
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        findViewById(android.R.id.content).setBackgroundColor(getColors(sIndex));

        AppCompatTextView textView = findViewById(R.id.text);
        textView.setText(getSimpleName());

        AppCompatButton button = findViewById(R.id.start);
        button.setOnClickListener(v -> {
            ++sIndex;
            startActivity(new Intent(getBaseContext(), MainActivity.class));
        });

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SlideLayout layout = mManager.getSlideLayout();
            if (layout == null) return;
            switch (checkedId) {
                case R.id.radioLeft:
                    layout.setTrackingEdge(EDGE_LEFT);
                    break;

                case R.id.radioRight:
                    layout.setTrackingEdge(EDGE_RIGHT);
                    break;

                case R.id.radioTop:
                    // Required themes of no ActionBar.
                    layout.setTrackingEdge(EDGE_TOP);
                    break;

                case R.id.radioBottom:
                    layout.setTrackingEdge(EDGE_BOTTOM);
                    break;
            }
        });

        // For activity except the first one.
        if (sInit) {
            ActionBar bar = getSupportActionBar();
            if (bar != null) bar.setDisplayHomeAsUpEnabled(true);
        } else {
            sInit = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getSimpleName() {
        String[] pkg = toString().split("\\.");
        return pkg[pkg.length - 1];
    }

    private static int getColors(int index) {
        int mod = index % 5;
        switch (mod) {
            case 0:
                return 0xFF33B5E5;
            case 1:
                return 0xFFAA66CC;
            case 2:
                return 0xFF99CC00;
            case 3:
                return 0xFFFFBB33;
            case 4:
                return 0xFFFF4444;
            default:
                return 0xFFFFFFFF;
        }
    }
}

