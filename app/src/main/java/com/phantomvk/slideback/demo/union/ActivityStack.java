package com.phantomvk.slideback.demo.union;

import android.app.Activity;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * {@link ActivityStack} for keeping references of all activities.
 * <p>
 * No like its name, {@link ActivityStack} keeps the {@link WeakReference} of activity's DecorView,
 * not activity itself.
 */
public final class ActivityStack {

    private static final List<WeakReference<View>> DECORS = new ArrayList<>(8);

    /**
     * Pushes a non-null activity to the stack.
     *
     * @param activity the activity instance which is needed to track its DecorView.
     */
    public static void push(Activity activity) {
        DECORS.add(new WeakReference<>(activity.getWindow().getDecorView()));
    }

    /**
     * Peeks the previous available DecorView.
     *
     * @return the view instance of DecorView
     */
    public static WeakReference<View> peek() {
        int index = DECORS.size() - 2;
        return index < 0 ? null : DECORS.get(index);
    }

    /**
     * In normal case, we will always matched the reference saved in the stack. The activity passed
     * to this method should not be null.
     *
     * @param activity the activity to pop, should not be null.
     */
    public static void pop(Activity activity) {
        ListIterator<WeakReference<View>> iterator = DECORS.listIterator();
        View decor = activity.getWindow().getDecorView();

        while (iterator.hasNext()) {
            View decorRef = iterator.next().get();

            // Found the target decorView, remove it right now.
            if (decorRef == decor) {
                iterator.remove();
                return;
            }
        }
    }
}
