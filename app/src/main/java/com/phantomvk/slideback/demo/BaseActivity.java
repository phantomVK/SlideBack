package com.phantomvk.slideback.demo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.phantomvk.slideback.SlideActivity;
import com.phantomvk.slideback.SlideLayout;

import static androidx.customview.widget.ViewDragHelper.EDGE_BOTTOM;
import static androidx.customview.widget.ViewDragHelper.EDGE_LEFT;
import static androidx.customview.widget.ViewDragHelper.EDGE_RIGHT;
import static androidx.customview.widget.ViewDragHelper.EDGE_TOP;

@SuppressLint("Registered")
public class BaseActivity extends SlideActivity {

    public static final String EXTRA_ACTION_BAR = "ACTION_BAR";
    public static final String EXTRA_INDEX = "ACTION_INDEX";

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        TextView textView = findViewById(R.id.text);
        textView.setText(getSimpleName());

        // Set background color to Activity.
        int index = getIntent().getIntExtra(EXTRA_INDEX, 0);
        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
        decorChild.getChildAt(0).setBackgroundColor(getColors(index));

        Button button = findViewById(R.id.start);
        button.setOnClickListener(v ->
                startActivity(new Intent(getBaseContext(), MainActivity.class)
                        .putExtra(EXTRA_INDEX, index + 1)
                        .putExtra(EXTRA_ACTION_BAR, true)));

        SlideLayout layout = mManager.getSlideLayout();
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (layout == null) return;
            switch (checkedId) {
                case R.id.radioLeft:
                    layout.setTrackingEdge(EDGE_LEFT);
                    break;

                case R.id.radioRight:
                    layout.setTrackingEdge(EDGE_RIGHT);
                    break;

                case R.id.radioTop:
                    layout.setTrackingEdge(EDGE_TOP);
                    break;

                case R.id.radioBottom:
                    layout.setTrackingEdge(EDGE_BOTTOM);
                    break;
            }
        });

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
        final String color;
        final int indexMod = index % 5;
        switch (indexMod) {
            case 0:
                color = "#33B5E5";
                break;
            case 1:
                color = "#AA66CC";
                break;
            case 2:
                color = "#99CC00";
                break;
            case 3:
                color = "#FFBB33";
                break;
            default:
                color = "#FF4444";
                break;
        }

        return Color.parseColor(color);
    }
}

