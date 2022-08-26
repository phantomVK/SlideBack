package com.phantomvk.slideback;

import android.os.Build;

import com.phantomvk.slideback.utility.TranslucentHelper;

public class ConductorImpl implements SlideManager.Conductor {

    @Override
    public boolean slideBackDisable() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || !TranslucentHelper.isEnabled();
    }
}
