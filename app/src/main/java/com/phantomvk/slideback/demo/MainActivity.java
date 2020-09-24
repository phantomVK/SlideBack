package com.phantomvk.slideback.demo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.phantomvk.slideback.SlideActivity;
import com.phantomvk.slideback.SlideLayout;
import com.phantomvk.slideback.SlideManager;
import com.phantomvk.slideback.demo.union.ActivityStack;
import com.phantomvk.slideback.demo.union.SlideAdapter;

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

public class MainActivity extends SlideActivity implements SlideAdapter.UnionMonitor {

    private static final Pattern PATTERN = Pattern.compile("\\.");
    private static final int COLOR = Color.parseColor("#FAFAFA");
    private static final int[] COLORS = {
            0xFF33B5E5, 0xFF00574B, 0xFFAA66CC, 0xFF99CC00,
            0xFFFFBB33, 0xFFFF4444, 0xFF008577, 0xFFD81B60};

    private static int sIndex = 0;
    private final String name = PATTERN.split(toString())[4];
    private int trackingEdge = EDGE_LEFT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(name, "onCreate");
        ActivityStack.add(this);

        if (!slideManager.isSlideDisable()) {
            overridePendingTransition(R.anim.slide_in_right, 0);
        }

        setWindow(getWindow());
        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(name, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(name, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(name, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(name, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(name, "onDestroy");
        ActivityStack.remove(this);
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
        if (!slideManager.isSlideDisable()) {
            overridePendingTransition(0, R.anim.slide_out_right);
        }
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(COLORS[sIndex++ & (8 - 1)]);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if (bar != null) bar.setDisplayHomeAsUpEnabled(true);

        AppCompatTextView textView = findViewById(R.id.text);
        textView.setText(name);

        AppCompatButton button = findViewById(R.id.start);
        button.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        final float density = getResources().getDisplayMetrics().density;
        final SlideLayout layout = slideManager.getSlideLayout();
        if (layout != null) layout.setEdgeSize((int) (density * 120));

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (layout == null) return;

            if (checkedId == R.id.radioLeft) {
                trackingEdge = EDGE_LEFT;
            } else if (checkedId == R.id.radioRight) {
                trackingEdge = EDGE_RIGHT;
            } else if (checkedId == R.id.radioTop) {
                trackingEdge = EDGE_TOP;
            } else if (checkedId == R.id.radioBottom) {
                trackingEdge = EDGE_BOTTOM;
            }

            layout.setTrackingEdge(trackingEdge);
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

    @NonNull
    @Override
    protected SlideManager slideManagerProvider() {
        return new SlideManager(this, new SlideAdapter(this));
    }

    @Override
    public boolean isUnionEnable() {
        return trackingEdge == EDGE_LEFT;
    }
}
