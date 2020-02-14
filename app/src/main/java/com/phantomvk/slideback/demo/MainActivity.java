package com.phantomvk.slideback.demo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.RadioGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.phantomvk.slideback.SlideActivity;
import com.phantomvk.slideback.SlideLayout;

import java.util.regex.Pattern;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
import static androidx.customview.widget.ViewDragHelper.EDGE_BOTTOM;
import static androidx.customview.widget.ViewDragHelper.EDGE_LEFT;
import static androidx.customview.widget.ViewDragHelper.EDGE_RIGHT;
import static androidx.customview.widget.ViewDragHelper.EDGE_TOP;

public class MainActivity extends SlideActivity {

    private static int sIndex = 0;
    private static final Pattern PATTERN = Pattern.compile("\\.");
    private static final int COLOR = Color.parseColor("#FAFAFA");
    private static final int[] COLORS = {
            0xFF33B5E5, 0xFF00574B, 0xFFAA66CC, 0xFF99CC00,
            0xFFFFBB33, 0xFFFF4444, 0xFF008577, 0xFFD81B60};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!slideBackDisable()) overridePendingTransition(R.anim.slide_in_right, 0);

        setWindow(getWindow());
        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        if (!slideBackDisable()) overridePendingTransition(0, R.anim.slide_out_right);
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(COLORS[sIndex++ & (8 - 1)]);

        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) bar.setDisplayHomeAsUpEnabled(true);

        AppCompatTextView textView = findViewById(R.id.text);
        textView.setText(PATTERN.split(toString())[4]);

        AppCompatButton button = findViewById(R.id.start);
        button.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

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

    private static void setWindow(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(COLOR);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(FLAG_TRANSLUCENT_STATUS);
        }
    }
}
