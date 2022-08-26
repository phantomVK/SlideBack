package com.phantomvk.slideback.demo.union;

import android.app.Activity;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * Keeping references of all activities.
 */
public final class ActivityStack {

    private static final ArrayList<Activity> sActivities = new ArrayList<>();

    /**
     * Pushes a non-null activity to the stack.
     */
    public static void push(@NonNull Activity activity) {
        sActivities.add(activity);
    }

    /**
     * Peeks the previous available activity.
     */
    public static Activity peek() {
        int index = sActivities.size() - 2;
        return (index < 0) ? null : sActivities.get(index);
    }

    /**
     * Pops the activity which at the top of the stack.
     */
    public static void pop() {
        int index = sActivities.size() - 1;
        sActivities.remove(index);
    }
}
