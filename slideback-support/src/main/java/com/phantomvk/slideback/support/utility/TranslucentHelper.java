package com.phantomvk.slideback.support.utility;

import android.app.Activity;
import android.app.ActivityOptions;
import android.support.annotation.Nullable;

import java.lang.reflect.Method;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

public class TranslucentHelper {

    private static Method sOptionsMethod;
    private static Method sInvokeMethod;
    private static Method sRevokeMethod;

    static {
        try {
            init();
        } catch (Throwable ignore) {
        }
    }

    private static void init() throws NoSuchMethodException {
        if (SDK_INT < KITKAT) return;

        Class<?>[] classes = Activity.class.getDeclaredClasses();
        Class<?> clz = null;
        for (final Class c : classes) {
            if (c.getSimpleName().equals("TranslucentConversionListener")) {
                clz = c;
                break;
            }
        }

        if (clz == null) return;
        if (SDK_INT >= LOLLIPOP) {
            sOptionsMethod = Activity.class.getDeclaredMethod("getActivityOptions");
            sOptionsMethod.setAccessible(true);

            sInvokeMethod = Activity.class.getDeclaredMethod("convertToTranslucent", clz, ActivityOptions.class);
            sInvokeMethod.setAccessible(true);
        } else {
            sInvokeMethod = Activity.class.getDeclaredMethod("convertToTranslucent", clz);
            sInvokeMethod.setAccessible(true);
        }

        sRevokeMethod = Activity.class.getDeclaredMethod("convertFromTranslucent");
        sRevokeMethod.setAccessible(true);
    }

    /**
     * Available since Android 4.4(API19).
     *
     * @param activity Activity
     */
    public static void setTranslucent(@Nullable final Activity activity) {
        if (SDK_INT < KITKAT || activity == null || sInvokeMethod == null) return;
        try {
            if (SDK_INT >= LOLLIPOP) {
                Object o = (sOptionsMethod == null) ? null : sOptionsMethod.invoke(activity);
                sInvokeMethod.invoke(activity, null, o);
            } else {
                sInvokeMethod.invoke(activity, new Object[]{null});
            }
        } catch (Throwable ignored) {
        }
    }

    /**
     * Available since Android 4.4(API19).
     *
     * @param activity Activity
     */
    public static void removeTranslucent(@Nullable final Activity activity) {
        if (SDK_INT < KITKAT || activity == null || sRevokeMethod == null) return;
        try {
            sRevokeMethod.invoke(activity);
        } catch (Throwable ignored) {
        }
    }
}
