package com.phantomvk.slideback.demo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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

    public static final String EXTRA_ACTION_BAR = "B";
    public static final String EXTRA_INDEX = "I";

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

        int index = getIntent().getIntExtra(EXTRA_INDEX, 0);
        findViewById(android.R.id.content).setBackgroundColor(getColors(index));

        AppCompatTextView textView = findViewById(R.id.text);
        textView.setText(getSimpleName());

        AppCompatButton button = findViewById(R.id.start);
        button.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), MainActivity.class)
                    .putExtra(EXTRA_INDEX, index + 1)
                    .putExtra(EXTRA_ACTION_BAR, true);
            startActivity(i);
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
        if (getIntent().getBooleanExtra(EXTRA_ACTION_BAR, false)) {
            ActionBar bar = getSupportActionBar();
            if (bar != null) bar.setDisplayHomeAsUpEnabled(true);
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
        String color;
        int mod = index % 5;
        switch (mod) {
            case 0:
                color = "#33B5E5";
                break;
            case 1:
                color = "#AA66CC";
                break;
            case 2:
                color = "#99CC00";
                break;
            default:
                color = "#FFBB33";
                break;
        }

        return Color.parseColor(color);
    }
}

