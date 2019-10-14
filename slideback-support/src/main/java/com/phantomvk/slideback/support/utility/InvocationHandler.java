package com.phantomvk.slideback.support.utility;

import java.lang.reflect.Method;

public class InvocationHandler implements java.lang.reflect.InvocationHandler {

    private TranslucentConversionListener listener;

    InvocationHandler(TranslucentConversionListener listener) {
        this.listener = listener;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (listener != null) listener.onTranslucentConversionComplete((boolean) args[0]);
        return null;
    }
}
