package com.phantomvk.slideback.demo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.customview.widget.ViewDragHelper;

import com.phantomvk.slideback.SlideActivity;
import com.phantomvk.slideback.SlideLayout;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
import static androidx.customview.widget.ViewDragHelper.EDGE_BOTTOM;
import static androidx.customview.widget.ViewDragHelper.EDGE_LEFT;
import static androidx.customview.widget.ViewDragHelper.EDGE_RIGHT;
import static androidx.customview.widget.ViewDragHelper.EDGE_TOP;

/**
 * This class contains nothing about {@link SlideActivity}.
 * Code moved here to make the subclass more easier to read.
 */
@SuppressLint("Registered")
public class BaseActivity extends SlideActivity {

    private static int sIndex = 0;
    private static final int[] mColors = {
            0xFF33B5E5, 0xFF00574B, 0xFFAA66CC, 0xFF99CC00,
            0xFFFFBB33, 0xFFFF4444, 0xFF008577, 0xFFD81B60};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindow();
        overridePendingTransition(R.anim.slide_in_right, 0);
        setContentView(R.layout.activity_main);
    }

    /**
     * {@link ViewDragHelper#EDGE_TOP} Required using 'Theme.AppCompat.Light.NoActionBar'
     * as the theme of activity, rather then 'Theme.AppCompat.Light.DarkActionBar' or any
     * other themes contain ActionBar, to avoid ActionBar consuming touch events when user
     * is sliding view.
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        toolbar.setBackgroundColor(mColors[sIndex++ & (8 - 1)]);

        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        AppCompatTextView textView = findViewById(R.id.text);
        textView.setText(toString().split("\\.")[4]);

        AppCompatButton button = findViewById(R.id.start);
        button.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), MainActivity.class)));

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SlideLayout layout = slideManager.getSlideLayout();
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
    }

    private void setWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            window.clearFlags(FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(FLAG_TRANSLUCENT_STATUS);
        }
    }

    public int getStatusBarHeight() {
        int id = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return (id > 0) ? getResources().getDimensionPixelSize(id) : 0;
    }
}
