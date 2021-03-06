package com.phantomvk.slideback.support.utility;

import android.support.annotation.NonNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TranslucentConversionHandler implements InvocationHandler {

    private final TranslucentConversionListener l;

    TranslucentConversionHandler(@NonNull TranslucentConversionListener listener) {
        l = listener;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if ((boolean) args[0]) l.onTranslucentConversionComplete(true);
        return null;
    }
}
