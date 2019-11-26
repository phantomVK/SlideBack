package com.phantomvk.slideback.support.utility;

import android.app.Activity;
import android.app.ActivityOptions;
import android.support.annotation.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Available since Android 4.4(API19).
 */
public class TranslucentHelper {

    private static Method sOptionsMethod;
    private static Method sInvokeMethod;
    private static Method sRevokeMethod;
    private static Class<?> sClz;

    static {
        try {
            init();
        } catch (Throwable ignore) {
        }
    }

    private static void init() throws NoSuchMethodException {
        if (SDK_INT < KITKAT) return;

        Class<?>[] classes = Activity.class.getDeclaredClasses();
        for (Class c : classes) {
            if (c.getSimpleName().equals("TranslucentConversionListener")) {
                sClz = c;
                break;
            }
        }

        if (sClz == null) return;
        if (SDK_INT >= LOLLIPOP) {
            sOptionsMethod = Activity.class.getDeclaredMethod("getActivityOptions");
            sOptionsMethod.setAccessible(true);

            sInvokeMethod = Activity.class.getDeclaredMethod("convertToTranslucent", sClz, ActivityOptions.class);
            sInvokeMethod.setAccessible(true);
        } else {
            sInvokeMethod = Activity.class.getDeclaredMethod("convertToTranslucent", sClz);
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
    public static void setTranslucent(@Nullable Activity activity,
                                      @Nullable TranslucentConversionListener listener) {
        if (SDK_INT < KITKAT || activity == null || sInvokeMethod == null) return;
        try {
            if (SDK_INT >= LOLLIPOP) {
                Object o = (sOptionsMethod == null) ? null : sOptionsMethod.invoke(activity);
                if (listener == null) {
                    sInvokeMethod.invoke(activity, null, o);
                } else {
                    Object obj = Proxy.newProxyInstance(Activity.class.getClassLoader(),
                            new Class[]{sClz},
                            new TranslucentConversionHandler(listener));
                    sInvokeMethod.invoke(activity, obj, o);
                }
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
    public static void removeTranslucent(@Nullable Activity activity) {
        if (SDK_INT < KITKAT || activity == null || sRevokeMethod == null) return;
        try {
            sRevokeMethod.invoke(activity);
        } catch (Throwable ignored) {
        }
    }
}
