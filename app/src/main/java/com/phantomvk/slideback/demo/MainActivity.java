package com.phantomvk.slideback.demo;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
import static androidx.customview.widget.ViewDragHelper.EDGE_BOTTOM;
import static androidx.customview.widget.ViewDragHelper.EDGE_LEFT;
import static androidx.customview.widget.ViewDragHelper.EDGE_RIGHT;
import static androidx.customview.widget.ViewDragHelper.EDGE_TOP;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.phantomvk.slideback.SlideLayout;
import com.phantomvk.slideback.demo.union.BaseActivity;

import java.util.regex.Pattern;

public class MainActivity extends BaseActivity {

    private static final Pattern PATTERN = Pattern.compile("\\.");
    private static final int COLOR = Color.parseColor("#FAFAFA");
    private static final int[] COLORS = {
            0xFF33B5E5, 0xFF00574B, 0xFFAA66CC, 0xFF99CC00,
            0xFFFFBB33, 0xFFFF4444, 0xFF008577, 0xFFD81B60
    };

    private static int sIndex = 0;
    private final String tag = PATTERN.split(toString())[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(tag, "onCreate()");

        setWindow();
        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(tag, "onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(tag, "onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(tag, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(tag, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(tag, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(tag, "onDestroy()");
    }

    private void setWindow() {
        Window window = getWindow();
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

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(COLORS[sIndex++ & (8 - 1)]);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if (bar != null) bar.setDisplayHomeAsUpEnabled(true);

        AppCompatTextView textView = findViewById(R.id.text);
        textView.setText(tag);

        AppCompatButton button = findViewById(R.id.start);
        button.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        final RadioGroup radioGroup = findViewById(R.id.radioGroup);
        final SlideLayout layout = (slideManager == null) ? null : slideManager.getSlideLayout();

        if (layout == null) {
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                String text = "No SlideManager or SlideLayout found.";
                Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            });
        } else {
            float density = getResources().getDisplayMetrics().density;
            layout.setEdgeSize((int) (density * 120));

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
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
    }
}
