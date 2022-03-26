package com.phantomvk.slideback.support.utility;

import android.app.Activity;
import android.app.ActivityOptions;
import android.support.annotation.NonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static android.os.Build.VERSION.SDK_INT;

public class TranslucentHelper {

    private static Method sOptionsMethod;
    private static Method sInvokeMethod;
    private static Method sRevokeMethod;
    private static Class<?>[] sClzArray;

    static {
        if (SDK_INT >= 19) {
            try {
                init();
            } catch (Throwable ignored) {
            }
        }
    }

    private static void init() throws NoSuchMethodException {
        for (Class<?> c : Activity.class.getDeclaredClasses()) {
            if (c.getSimpleName().equals("TranslucentConversionListener")) {
                sClzArray = new Class[]{c};
                break;
            }
        }

        if (sClzArray == null) {
            return;
        }

        if (SDK_INT >= 21) {
            sOptionsMethod = Activity.class.getDeclaredMethod("getActivityOptions");
            sOptionsMethod.setAccessible(true);

            sInvokeMethod = Activity.class.getDeclaredMethod("convertToTranslucent", sClzArray[0], ActivityOptions.class);
        } else {
            sInvokeMethod = Activity.class.getDeclaredMethod("convertToTranslucent", sClzArray[0]);
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

        if (SDK_INT < 19 || sInvokeMethod == null) {
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
        if (SDK_INT < 19) {
            return;
        }

        if (sRevokeMethod != null) {
            try {
                sRevokeMethod.invoke(activity);
            } catch (Throwable ignored) {
            }
        }
    }
}
