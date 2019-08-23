package com.phantomvk.slideback.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.customview.widget.ViewDragHelper;

import com.phantomvk.slideback.SlideActivity;
import com.phantomvk.slideback.SlideLayout;

import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
import static androidx.customview.widget.ViewDragHelper.EDGE_BOTTOM;
import static androidx.customview.widget.ViewDragHelper.EDGE_LEFT;
import static androidx.customview.widget.ViewDragHelper.EDGE_RIGHT;
import static androidx.customview.widget.ViewDragHelper.EDGE_TOP;

@SuppressLint("Registered")
public class BaseActivity extends SlideActivity {

    private static int sIndex = 0;
    private static final int[] mColors = {0xFF33B5E5, 0xFF00574B, 0xFFAA66CC, 0xFF99CC00,
            0xFFFFBB33, 0xFFFF4444, 0xFF008577, 0xFFD81B60};

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
        setWindow(getWindow());

        View contentView = findViewById(android.R.id.content);
        contentView.setBackgroundColor(mColors[sIndex++ & (8 - 1)]);

        AppCompatTextView textView = findViewById(R.id.text);
        textView.setText(getSimpleName(this));

        AppCompatButton button = findViewById(R.id.start);
        button.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), MainActivity.class)));

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static String getSimpleName(Activity activity) {
        String[] pkg = activity.toString().split("\\.");
        return pkg[pkg.length - 1];
    }

    public static void setWindow(Window w) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;
        w.setFlags(FLAG_TRANSLUCENT_NAVIGATION, FLAG_TRANSLUCENT_NAVIGATION);
        w.setFlags(FLAG_TRANSLUCENT_STATUS, FLAG_TRANSLUCENT_STATUS);
    }
}

