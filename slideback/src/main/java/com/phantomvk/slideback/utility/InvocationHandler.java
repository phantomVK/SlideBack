package com.phantomvk.slideback.utility;

import java.lang.reflect.Method;

public class InvocationHandler implements java.lang.reflect.InvocationHandler {

    private TranslucentConversionListener l;

    InvocationHandler(TranslucentConversionListener listener) {
        l = listener;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if ((l != null) && (boolean) args[0]) l.onTranslucentConversionComplete(true);
        return null;
    }
}
