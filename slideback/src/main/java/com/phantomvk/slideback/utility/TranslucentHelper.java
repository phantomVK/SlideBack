package com.phantomvk.slideback.utility;

import static android.os.Build.VERSION.SDK_INT;

import android.app.Activity;
import android.app.ActivityOptions;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class TranslucentHelper {

    private static boolean sEnabled = false;
    private static Class<?>[] sClzArray;
    private static Method sOptionsMethod;
    private static Method sInvokeMethod;
    private static Method sRevokeMethod;

    static {
        if (SDK_INT >= 19) {
            try {
                init();
                sEnabled = true;
            } catch (Throwable t) {
                sEnabled = false;
                sClzArray = null;
                sOptionsMethod = null;
                sInvokeMethod = null;
                sRevokeMethod = null;
            }
        }
    }

    private static void init() throws NoSuchMethodException, ClassNotFoundException {
        Class<?> clazz = Class.forName("android.app.Activity$TranslucentConversionListener");
        sClzArray = new Class[]{clazz};

        if (SDK_INT >= 21) {
            sOptionsMethod = Activity.class.getDeclaredMethod("getActivityOptions");
            sOptionsMethod.setAccessible(true);

            sInvokeMethod = Activity.class.getDeclaredMethod("convertToTranslucent", clazz, ActivityOptions.class);
        } else {
            sInvokeMethod = Activity.class.getDeclaredMethod("convertToTranslucent", clazz);
        }

        sInvokeMethod.setAccessible(true);

        sRevokeMethod = Activity.class.getDeclaredMethod("convertFromTranslucent");
        sRevokeMethod.setAccessible(true);
    }

    /**
     * Available since Android 4.4(API19).
     */
    public static void setTranslucent(@NonNull Activity activity,
                                      @NonNull TranslucentConversionListener listener) {

        if (SDK_INT < 19 || !sEnabled || sInvokeMethod == null) {
            return;
        }

        try {
            if (SDK_INT >= 21) {
                TranslucentConversionHandler h = new TranslucentConversionHandler(listener);
                Object p = Proxy.newProxyInstance(Activity.class.getClassLoader(), sClzArray, h);
                Object o = (sOptionsMethod == null) ? null : sOptionsMethod.invoke(activity);

                sInvokeMethod.invoke(activity, p, o);
            } else {
                // For API19 and API20.
                sInvokeMethod.invoke(activity, new Object[]{null});
                listener.onTranslucentConversionComplete(true);
            }
        } catch (Throwable ignored) {
        }
    }

    /**
     * Available since Android 4.4(API19).
     */
    public static void removeTranslucent(@NonNull Activity activity) {
        if (SDK_INT < 19 || !sEnabled) {
            return;
        }

        if (SDK_INT >= 30) {
            activity.setTranslucent(false);
            return;
        }

        // For API19 to API29.
        if (sRevokeMethod != null) {
            try {
                sRevokeMethod.invoke(activity);
            } catch (Throwable ignored) {
            }
        }
    }

    public static boolean isEnabled() {
        return sEnabled;
    }
}
