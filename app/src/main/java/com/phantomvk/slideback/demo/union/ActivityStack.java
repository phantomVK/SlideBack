package com.phantomvk.slideback.demo.union;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ActivityStack} for keeping references of all activities.
 * <p>
 * No like its name, {@link ActivityStack} keeps the reference of activity's DecorView,
 * not activity itself.
 */
public final class ActivityStack {

    private static final List<View> DECORS = new ArrayList<>();

    /**
     * Pushes a non-null activity to the stack.
     *
     * @param activity the activity instance which is needed to track its DecorView.
     */
    public static void push(@NonNull Activity activity) {
        DECORS.add(activity.getWindow().getDecorView());
    }

    /**
     * Peeks the previous available DecorView.
     *
     * @return the view instance of DecorView
     */
    public static View peek() {
        int index = DECORS.size() - 2;
        return index < 0 ? null : DECORS.get(index);
    }

    /**
     * Removes the DecorView which at the top of the stack.
     */
    public static void pop() {
        DECORS.remove(DECORS.size() - 1);
    }
}
