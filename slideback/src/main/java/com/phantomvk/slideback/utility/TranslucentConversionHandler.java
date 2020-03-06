package com.phantomvk.slideback.utility;

import androidx.annotation.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TranslucentConversionHandler implements InvocationHandler {

    private final TranslucentConversionListener l;

    TranslucentConversionHandler(@Nullable TranslucentConversionListener listener) {
        l = listener;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if ((boolean) args[0] && (l != null)) l.onTranslucentConversionComplete(true);
        return null;
    }
}
